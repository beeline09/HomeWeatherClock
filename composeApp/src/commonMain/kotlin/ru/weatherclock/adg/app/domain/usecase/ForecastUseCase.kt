package ru.weatherclock.adg.app.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import ru.weatherclock.adg.app.data.dto.WeatherConfig
import ru.weatherclock.adg.app.data.dto.WeatherFetchState
import ru.weatherclock.adg.app.data.dto.WeatherServer
import ru.weatherclock.adg.app.data.error.NoWeatherApiKeyException
import ru.weatherclock.adg.app.data.util.atEndOfDay
import ru.weatherclock.adg.app.data.util.atStartOfDay
import kotlin.time.Duration.Companion.hours
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.AccuweatherForecastDto
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.DetailDto
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbModel
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbModelEvapotranspiration
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbModelIce
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbModelRain
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbModelSnow
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbModelSolarIrradiance
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbModelTotalLiquid
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbModelWind
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbModelWindGust
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbRealFeelTemperature
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbRealFeelTemperatureShade
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asAccuweatherDbTemperature
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.asDomainModel
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.OpenWeatherMapForecastDto
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.detail.PartOfDay
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.detail.asDbModel
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.detail.toPartOfDay
import ru.weatherclock.adg.app.data.repository.db.forecast.AccuweatherDbRepository
import ru.weatherclock.adg.app.data.repository.db.forecast.OpenWeatherMapDbRepository
import ru.weatherclock.adg.app.data.repository.settings.WeatherSettingsRepository
import ru.weatherclock.adg.app.data.repository.weather.implementation.AccuweatherForecastRepositoryImpl
import ru.weatherclock.adg.app.data.repository.weather.implementation.OpenWeatherMapForecastRepositoryImpl
import ru.weatherclock.adg.app.data.util.fromDbToLocalDateTime
import ru.weatherclock.adg.app.data.util.isEqualsByHour
import ru.weatherclock.adg.app.domain.model.forecast.DayDetail
import ru.weatherclock.adg.app.domain.model.forecast.DetailType
import ru.weatherclock.adg.app.domain.model.forecast.Forecast
import ru.weatherclock.adg.app.domain.model.forecast.ForecastDay
import ru.weatherclock.adg.app.domain.model.forecast.Severity
import ru.weatherclock.adg.app.domain.model.forecast.asDomainModel
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.db.OpenWeatherMap.ForecastItem
import ru.weatherclock.adg.db.OpenWeatherMap.Rain
import ru.weatherclock.adg.db.OpenWeatherMap.Snow

class ForecastUseCase(
    private val accuweatherRepository: AccuweatherForecastRepositoryImpl,
    private val openWeatherMapRepository: OpenWeatherMapForecastRepositoryImpl,
    private val accuweatherDbRepository: AccuweatherDbRepository,
    private val openWeatherMapDbRepository: OpenWeatherMapDbRepository,
    private val weatherSettingsRepository: WeatherSettingsRepository,
    appScope: CoroutineScope,
) {

    /**
     * События для UI (тосты): например, «API-ключ не введён». Сетевые ошибки
     * сюда НЕ попадают — они тихо фолбэкаются на кеш БД.
     */
    private val _errors = MutableSharedFlow<Throwable>(extraBufferCapacity = 1)
    val errors: SharedFlow<Throwable> = _errors

    /** Single-flight: параллельные триггеры не дают второго сетевого запроса */
    private val fetchMutex = Mutex()

    /**
     * Самообновляющийся источник прогноза (уровень B):
     * подписки на config — flatMapLatest (смена сервера/города/ключей →
     * перезапуск цикла), внутри — fetchOrCache и часовая пауза.
     * WhileSubscribed(5s): пока никого не интересует погода (экран закрыт),
     * цикл спит и сеть не дёргается вовсе — экономия квот и батареи.
     */
    val forecast: StateFlow<Forecast?> = weatherSettingsRepository.config
        .onStart { emit(weatherSettingsRepository.getConfig()) }
        .distinctUntilChanged()
        .flatMapLatest {
            flow {
                while (true) {
                    emit(fetchOrCache())
                    delay(FETCH_INTERVAL)
                }
            }
        }
        .stateIn(appScope, SharingStarted.WhileSubscribed(5_000), null)

    /**
     * Политика обновления (TTL, переживает перезапуск приложения — метки
     * персистятся в AppSettings.weatherFetchState):
     * 1. БД пуста → сеть (всегда);
     * 2. успешный fetch был позже FETCH_INTERVAL назад → БД без сети;
     * 3. конфиг с последнего успешного fetch изменился (сервер/город/ключи/
     *    единицы/язык) → сеть даже при свежем кеше.
     */
    private suspend fun fetchOrCache(): Forecast? {
        if (!weatherSettingsRepository.isWeatherEnabled()) return null
        val now = LocalDateTime.now()
        val cached = getForecastInternal(
            startDate = now.atStartOfDay(),
            endDate = now.atEndOfDay().date.plus(DatePeriod(days = 5)).atTime(23, 59, 59)
        )
        val fetchState = weatherSettingsRepository.getFetchState()
        val config = weatherSettingsRepository.getConfig()
        val nowEpoch = now.toEpochSeconds()
        val cacheIsFresh = fetchState.lastFetchedConfig == config &&
                (nowEpoch - fetchState.lastSuccessEpochSeconds) < FETCH_INTERVAL.inWholeSeconds
        if (cacheIsFresh && !cached?.dailyForecast.isNullOrEmpty()) {
            return cached
        }
        return fetchMutex.withLock {
            // Метки могли обновиться, пока ждали мьютекс — перепроверяем
            val stateAfterLock = weatherSettingsRepository.getFetchState()
            val configAfterLock = weatherSettingsRepository.getConfig()
            val freshAfterLock = stateAfterLock.lastFetchedConfig == configAfterLock &&
                    (nowEpoch - stateAfterLock.lastSuccessEpochSeconds) < FETCH_INTERVAL.inWholeSeconds
            if (freshAfterLock && !cached?.dailyForecast.isNullOrEmpty()) return@withLock cached
            try {
                val fresh = getForecastRemote()
                weatherSettingsRepository.setFetchState(
                    WeatherFetchState(
                        lastFetchedConfig = configAfterLock,
                        lastSuccessEpochSeconds = nowEpoch
                    )
                )
                fresh
            } catch (e: NoWeatherApiKeyException) {
                // Ключ не введён: метки не трогаем (после ввода ключа —
                // немедленный рефреш через плоскую подписку на конфиг)
                _errors.tryEmit(e)
                cached
            } catch (e: Exception) {
                // Сеть/лимиты/парсинг: тихий фолбэк на кеш, ретрай через час
                println("Forecast download failed, fallback to DB cache: ${e.message}")
                e.printStackTrace()
                cached
            }
        }
    }

    private suspend fun getAccuweatherForecastRemote(): AccuweatherForecastDto {
        return accuweatherRepository.getWeatherForecast(
            cityKey = weatherSettingsRepository.getCityKey(),
            language = weatherSettingsRepository.getWeatherLanguage().code,
            apiKeys = weatherSettingsRepository.getApiKeys()
        )
    }

    private suspend fun getOpenWeatherMapForecastRemote(): OpenWeatherMapForecastDto {
        return openWeatherMapRepository.getWeatherForecast(
            latitude = weatherSettingsRepository.getLatitude(),
            longitude = weatherSettingsRepository.getLongitude(),
            language = weatherSettingsRepository.getWeatherLanguage().code,
            apiKeys = weatherSettingsRepository.getApiKeys(),
            units = weatherSettingsRepository.getUnitType()
        )
    }

    private suspend fun getForecastRemote(): Forecast {
        println("ForecastUseCase: загрузка прогноза из СЕТИ (${weatherSettingsRepository.getWeatherServer()})")
        return when (weatherSettingsRepository.getConfig().server) {
            WeatherServer.Accuweather -> {
                getAccuweatherForecastRemote().run {
                    saveToDb(weatherSettingsRepository.getCityKey())
                    asDomainModel()
                }
            }

            WeatherServer.OpenWeatherMap -> {
                getOpenWeatherMapForecastRemote().run {
                    saveToDb(
                        latitude = weatherSettingsRepository.getLatitude(),
                        longitude = weatherSettingsRepository.getLongitude()
                    )
                    asDomainModel()
                }
            }
        }
    }

    companion object {
        /** Период самообновления и он же TTL кеша: 1 час */
        val FETCH_INTERVAL = 1.hours

        /** kotlin.time.Clock в Kotlin 2.2 ещё experimental — epoch через toInstant */
        private fun LocalDateTime.toEpochSeconds(): Long =
            toInstant(TimeZone.currentSystemDefault()).epochSeconds
    }

    private suspend fun AccuweatherForecastDto.saveToDb(forecastKey: String) {
        headline?.asAccuweatherDbModel(forecastKey)
            ?.let { accuweatherDbRepository.insertHeadline(it) }
        for (f in dailyForecasts) {
            val forecastPid = accuweatherDbRepository.insertDailyForecast(
                dailyForecast = f.asAccuweatherDbModel(
                    forecastKey
                )
            )
            if (forecastPid >= 0L) {
                f.sun?.asAccuweatherDbModel(forecastPid = forecastPid)?.let {
                    accuweatherDbRepository.insertSun(
                        forecastPid = forecastPid, sun = it
                    )
                }
                f.moon?.asAccuweatherDbModel(forecastPid = forecastPid)?.let {
                    accuweatherDbRepository.insertMoon(
                        forecastPid = forecastPid, it
                    )
                }
                f.temperature?.asAccuweatherDbTemperature(forecastPid = forecastPid)?.let {
                    accuweatherDbRepository.insertTemperature(
                        forecastPid = forecastPid, it
                    )
                }
                f.realFeelTemperature?.asAccuweatherDbRealFeelTemperature(forecastPid = forecastPid)
                    ?.let {
                        accuweatherDbRepository.insertRealFeelTemperature(
                            forecastPid = forecastPid, it
                        )
                    }
                f.realFeelTemperatureShade?.asAccuweatherDbRealFeelTemperatureShade(forecastPid = forecastPid)
                    ?.let {
                        accuweatherDbRepository.insertRealFeelTemperatureShade(
                            forecastPid = forecastPid, it
                        )
                    }
                f.degreeDaySummary?.asAccuweatherDbModel(forecastPid = forecastPid)?.let {
                    accuweatherDbRepository.insertDegreeDaySummary(
                        forecastPid = forecastPid, it
                    )
                }
                f.airAndPollen?.let {
                    accuweatherDbRepository.insertAirAndPollen(airAndPollens = it.map { airAndPollenDto ->
                        airAndPollenDto.asAccuweatherDbModel(
                            forecastPid = forecastPid
                        )
                    })
                }
                f.day?.apply {
                    insertDayDetailInternal(
                        forecastPid = forecastPid, type = DetailType.DAY
                    )
                }
                f.night?.apply {
                    insertDayDetailInternal(
                        forecastPid = forecastPid, type = DetailType.NIGHT
                    )
                }
            }
        }
    }

    private suspend fun DetailDto.insertDayDetailInternal(
        forecastPid: Long, type: DetailType
    ): Long {
        val detailPid = accuweatherDbRepository.insertDetail(
            forecastPid = forecastPid, type = type, forecastDetail = asAccuweatherDbModel(
                forecastPid = forecastPid, isNight = type == DetailType.NIGHT
            )
        )
        if (detailPid >= 0L) {
            wind?.let { wind ->
                accuweatherDbRepository.insertWind(
                    detailPid = detailPid, wind.asAccuweatherDbModelWind(detailPid = detailPid)
                )
            }
            windGust?.let { windGust ->
                accuweatherDbRepository.insertWindGust(
                    detailPid = detailPid,
                    windGust.asAccuweatherDbModelWindGust(detailPid = detailPid)
                )
            }
            totalLiquid?.let { totalLiquid ->
                accuweatherDbRepository.insertTotalLiquid(
                    detailPid = detailPid,
                    totalLiquid.asAccuweatherDbModelTotalLiquid(detailPid = detailPid)
                )
            }
            rain?.let { rain ->
                accuweatherDbRepository.insertRain(
                    detailPid = detailPid, rain.asAccuweatherDbModelRain(detailPid = detailPid)
                )
            }
            snow?.let { snow ->
                accuweatherDbRepository.insertSnow(
                    detailPid = detailPid, snow.asAccuweatherDbModelSnow(detailPid = detailPid)
                )
            }
            ice?.let { ice ->
                accuweatherDbRepository.insertIce(
                    detailPid = detailPid, ice.asAccuweatherDbModelIce(detailPid = detailPid)
                )
            }
            evapotranspiration?.let { e ->
                accuweatherDbRepository.insertEvapotranspiration(
                    detailPid = detailPid,
                    e.asAccuweatherDbModelEvapotranspiration(detailPid = detailPid)
                )
            }
            solarIrradiance?.let { si ->
                accuweatherDbRepository.insertSolarIrradiance(
                    detailPid = detailPid,
                    si.asAccuweatherDbModelSolarIrradiance(detailPid = detailPid)
                )
            }
        }
        return detailPid
    }

    private suspend fun getForecastInternal(
        startDate: LocalDateTime, endDate: LocalDateTime
    ): Forecast? = when (weatherSettingsRepository.getConfig().server) {
        WeatherServer.Accuweather -> getAccuweatherForecastInternal(
            startDate.date, endDate.date
        )

        WeatherServer.OpenWeatherMap -> getOpenWeatherMapForecastInternal(
            startDate, endDate
        )
    }

    private suspend fun getAccuweatherForecastInternal(
        startDate: LocalDate, endDate: LocalDate
    ): Forecast? {
        val headline = accuweatherDbRepository.getHeadline(
            weatherSettingsRepository.getCityKey(), startDate, endDate
        )
        val forecast = accuweatherDbRepository.getForecast(
            weatherSettingsRepository.getCityKey(), startDate, endDate
        ).mapNotNull {
            it.asDomainModel(
                temperature = accuweatherDbRepository.getTemperature(byForecastPid = it.pid),
                detailDay = accuweatherDbRepository.getDetail(
                    byForecastPid = it.pid, DetailType.DAY
                ),
                detailNight = accuweatherDbRepository.getDetail(
                    byForecastPid = it.pid, DetailType.NIGHT
                )
            )
        }
        return Forecast(
            headline = headline?.text,
            severity = Severity.entries[headline?.severity ?: 0],
            dailyForecast = forecast
        ).takeIf { headline != null || forecast.isNotEmpty() }
    }

    private suspend fun getOpenWeatherMapForecastInternal(
        startDate: LocalDateTime, endDate: LocalDateTime
    ): Forecast? {
        val forecastItems = openWeatherMapDbRepository.getForecast(
            latitude = weatherSettingsRepository.getLatitude(),
            longitude = weatherSettingsRepository.getLongitude(),
            startDate = startDate,
            endDate = endDate
        )
        return forecastItems.asDomainModel().takeIf { forecastItems.isNotEmpty() }
    }

    private suspend fun OpenWeatherMapForecastDto.saveToDb(
        latitude: Double, longitude: Double
    ) {
        items.forEach { forecastItem ->
            val forecastPid = openWeatherMapDbRepository.insertDailyForecast(
                forecastItem.asDbModel(
                    latitude = latitude, longitude = longitude
                )
            )
            if (forecastPid > 0L) {
                openWeatherMapDbRepository.insertMainInfo(
                    info = forecastItem.main.asDbModel(forecastPid = forecastPid)
                )
                forecastItem.weather.forEach { weatherIcon ->
                    openWeatherMapDbRepository.insertWeatherIcon(
                        icon = weatherIcon.asDbModel(forecastPid = forecastPid)
                    )
                }
                forecastItem.wind?.let {
                    openWeatherMapDbRepository.insertWind(
                        wind = it.asDbModel(forecastPid = forecastPid)
                    )
                }
                forecastItem.rain.forEach {
                    openWeatherMapDbRepository.insertRain(
                        rain = Rain(
                            forecast_pid = forecastPid, duration_h = it.key, count = it.value
                        )
                    )
                }
                forecastItem.snow.forEach {
                    openWeatherMapDbRepository.insertSnow(
                        snow = Snow(
                            forecast_pid = forecastPid, duration_h = it.key, count = it.value
                        )
                    )
                }
            }
        }
    }

    private suspend fun List<ForecastItem>.asDomainModel(): Forecast {
        val items = this
        val byDays = items.groupBy {
            it.date_time.fromDbToLocalDateTime().date
        }.filter { it.value.isNotEmpty() }.mapNotNull {
            val date = it.key
            val dayValues =
                it.value.filter { item -> item.part_of_day.toPartOfDay() == PartOfDay.Day }
            val nightValues =
                it.value.filter { item -> item.part_of_day.toPartOfDay() == PartOfDay.Night }
            if (dayValues.isEmpty() || nightValues.isEmpty()) {
                null
            } else {
                val nightList = nightValues.map { item ->
                    openWeatherMapDbRepository.getMainInfo(byForecastPid = item.pid)
                }
                val dayList = dayValues.map { item ->
                    openWeatherMapDbRepository.getMainInfo(byForecastPid = item.pid)
                }
                val nightInfo = nightList.minBy { info -> info.temperature }
                val dayInfo = dayList.maxBy { info -> info.temperature }
                val nightWeatherIcon =
                    openWeatherMapDbRepository.getWeatherIcon(byForecastPid = nightInfo.forecast_pid)
                        .firstOrNull()
                val dayWeatherIcon =
                    openWeatherMapDbRepository.getWeatherIcon(byForecastPid = dayInfo.forecast_pid)
                        .firstOrNull()
                ForecastDay(
                    date = date, min = DayDetail(
                        temperature = nightInfo.temperature,
                        icon = nightWeatherIcon?.icon.orEmpty(),
                        iconPhrase = nightWeatherIcon?.description
                    ), max = DayDetail(
                        temperature = dayInfo.temperature,
                        icon = dayWeatherIcon?.icon.orEmpty(),
                        iconPhrase = dayWeatherIcon?.description
                    )
                )
            }

        }
        return Forecast(dailyForecast = byDays)
    }
}
