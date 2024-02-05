package ru.weatherclock.adg.app.domain.usecase

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.app.data.dto.WeatherServer
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
) {

    private var lastUpdateForecast: LocalDateTime? = null

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
        return when (weatherSettingsRepository.getConfig().server) {
            WeatherServer.Accuweather -> {
                getAccuweatherForecastRemote().let {
                    it.saveToDb(weatherSettingsRepository.getCityKey())
                    it.asDomainModel()
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

    suspend fun getForPeriod(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Forecast? {

        val forecastInternal = getForecastInternal(
            startDate,
            endDate
        )

        //Логика обновления данных такая:
        //Если в БД пусто
        //Если в БД пусто и больше часа ничего не загружалось, либо это в первый раз
        //Если в БД не пусто и прошло больше 2 часов с последнего обновления
        val currentTime = LocalDateTime.now()
        //Если это в первый раз после запуска и в БД меньше 5 записей
        val us0 = lastUpdateForecast == null && (forecastInternal?.dailyForecast?.size ?: 0) < 5
        //С последнего обновления прошло больше 2 часов
        val us1 = lastUpdateForecast != null && lastUpdateForecast?.isEqualsByHour(
            currentTime,
            1
        ) != true
        //Если в БД пусто и данные не запрашивались больше часа
        val us2 =
            lastUpdateForecast != null && forecastInternal?.dailyForecast.isNullOrEmpty() && lastUpdateForecast?.isEqualsByHour(currentTime) != true
        val result = if (us0 || us1 || us2) {
            return try {
                getForecastRemote().also {
                    lastUpdateForecast = currentTime
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return forecastInternal
            }
        } else forecastInternal
        return result
    }

    private suspend fun AccuweatherForecastDto.saveToDb(forecastKey: String) {
        headline
            ?.asAccuweatherDbModel(forecastKey)
            ?.let { accuweatherDbRepository.insertHeadline(it) }
        for (f in dailyForecasts) {
            val forecastPid =
                accuweatherDbRepository.insertDailyForecast(dailyForecast = f.asAccuweatherDbModel(forecastKey))
            if (forecastPid >= 0L) {
                f.sun?.asAccuweatherDbModel(forecastPid = forecastPid)?.let {
                    accuweatherDbRepository.insertSun(
                        forecastPid = forecastPid,
                        sun = it
                    )
                }
                f.moon?.asAccuweatherDbModel(forecastPid = forecastPid)?.let {
                    accuweatherDbRepository.insertMoon(
                        forecastPid = forecastPid,
                        it
                    )
                }
                f.temperature?.asAccuweatherDbTemperature(forecastPid = forecastPid)?.let {
                    accuweatherDbRepository.insertTemperature(
                        forecastPid = forecastPid,
                        it
                    )
                }
                f.realFeelTemperature
                    ?.asAccuweatherDbRealFeelTemperature(forecastPid = forecastPid)
                    ?.let {
                        accuweatherDbRepository.insertRealFeelTemperature(
                            forecastPid = forecastPid,
                            it
                        )
                    }
                f.realFeelTemperatureShade
                    ?.asAccuweatherDbRealFeelTemperatureShade(forecastPid = forecastPid)
                    ?.let {
                        accuweatherDbRepository.insertRealFeelTemperatureShade(
                            forecastPid = forecastPid,
                            it
                        )
                    }
                f.degreeDaySummary?.asAccuweatherDbModel(forecastPid = forecastPid)?.let {
                    accuweatherDbRepository.insertDegreeDaySummary(
                        forecastPid = forecastPid,
                        it
                    )
                }
                f.airAndPollen?.let {
                    accuweatherDbRepository.insertAirAndPollen(airAndPollens = it.map { it.asAccuweatherDbModel(forecastPid = forecastPid) })
                }
                f.day?.apply {
                    insertDayDetailInternal(
                        forecastPid = forecastPid,
                        type = DetailType.DAY
                    )
                }
                f.night?.apply {
                    insertDayDetailInternal(
                        forecastPid = forecastPid,
                        type = DetailType.NIGHT
                    )
                }
            }
        }
    }

    private suspend fun DetailDto.insertDayDetailInternal(
        forecastPid: Long,
        type: DetailType
    ): Long {
        val detailPid = accuweatherDbRepository.insertDetail(
            forecastPid = forecastPid,
            type = type,
            forecastDetail = asAccuweatherDbModel(
                forecastPid = forecastPid,
                isNight = type == DetailType.NIGHT
            )
        )
        if (detailPid >= 0L) {
            wind?.let { wind ->
                accuweatherDbRepository.insertWind(
                    detailPid = detailPid,
                    wind.asAccuweatherDbModelWind(detailPid = detailPid)
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
                    detailPid = detailPid,
                    rain.asAccuweatherDbModelRain(detailPid = detailPid)
                )
            }
            snow?.let { snow ->
                accuweatherDbRepository.insertSnow(
                    detailPid = detailPid,
                    snow.asAccuweatherDbModelSnow(detailPid = detailPid)
                )
            }
            ice?.let { ice ->
                accuweatherDbRepository.insertIce(
                    detailPid = detailPid,
                    ice.asAccuweatherDbModelIce(detailPid = detailPid)
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
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Forecast? = when (weatherSettingsRepository.getConfig().server) {
        WeatherServer.Accuweather -> getAccuweatherForecastInternal(
            startDate.date,
            endDate.date
        )

        WeatherServer.OpenWeatherMap -> getOpenWeatherMapForecastInternal(
            startDate,
            endDate
        )
    }

    private suspend fun getAccuweatherForecastInternal(
        startDate: LocalDate,
        endDate: LocalDate
    ): Forecast? {
        val headline = accuweatherDbRepository.getHeadline(
            weatherSettingsRepository.getCityKey(),
            startDate,
            endDate
        )
        val forecast = accuweatherDbRepository.getForecast(
            weatherSettingsRepository.getCityKey(),
            startDate,
            endDate
        ).mapNotNull {
            it.asDomainModel(
                temperature = accuweatherDbRepository.getTemperature(byForecastPid = it.pid),
                detailDay = accuweatherDbRepository.getDetail(
                    byForecastPid = it.pid,
                    DetailType.DAY
                ),
                detailNight = accuweatherDbRepository.getDetail(
                    byForecastPid = it.pid,
                    DetailType.NIGHT
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
        startDate: LocalDateTime,
        endDate: LocalDateTime
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
        latitude: Double,
        longitude: Double
    ) {
        items.forEach { forecastItem ->
            val forecastPid = openWeatherMapDbRepository.insertDailyForecast(
                forecastItem.asDbModel(
                    latitude = latitude,
                    longitude = longitude
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
                            forecast_pid = forecastPid,
                            duration_h = it.key,
                            count = it.value
                        )
                    )
                }
                forecastItem.snow.forEach {
                    openWeatherMapDbRepository.insertSnow(
                        snow = Snow(
                            forecast_pid = forecastPid,
                            duration_h = it.key,
                            count = it.value
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
                val nightWeatherIcon = openWeatherMapDbRepository
                    .getWeatherIcon(byForecastPid = nightInfo.forecast_pid)
                    .firstOrNull()
                val dayWeatherIcon = openWeatherMapDbRepository
                    .getWeatherIcon(byForecastPid = dayInfo.forecast_pid)
                    .firstOrNull()
                ForecastDay(
                    date = date,
                    min = DayDetail(
                        temperature = nightInfo.temperature,
                        icon = nightWeatherIcon?.icon.orEmpty(),
                        iconPhrase = nightWeatherIcon?.description
                    ),
                    max = DayDetail(
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
