package ru.weatherclock.adg.app.domain.usecase

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.app.data.dto.WeatherConfigData
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.AccuweatherForecastDto
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.DetailDto
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbModel
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbModelEvapotranspiration
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbModelIce
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbModelRain
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbModelSnow
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbModelSolarIrradiance
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbModelTotalLiquid
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbModelWind
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbModelWindGust
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbRealFeelTemperature
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbRealFeelTemperatureShade
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asAccuweatherDbTemperature
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast.asDomainModel
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.OpenWeatherMapForecastDto
import ru.weatherclock.adg.app.data.repository.db.forecast.AccuweatherDbRepository
import ru.weatherclock.adg.app.data.repository.settings.WeatherSettingsRepository
import ru.weatherclock.adg.app.data.repository.weather.implementation.AccuweatherForecastRepositoryImpl
import ru.weatherclock.adg.app.data.repository.weather.implementation.OpenWeatherMapForecastRepositoryImpl
import ru.weatherclock.adg.app.data.util.isEqualsByHour
import ru.weatherclock.adg.app.domain.model.forecast.DetailType
import ru.weatherclock.adg.app.domain.model.forecast.Forecast
import ru.weatherclock.adg.app.domain.model.forecast.Severity
import ru.weatherclock.adg.app.domain.model.forecast.asDomainModel
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now

class ForecastUseCase(
    private val accuweatherRepository: AccuweatherForecastRepositoryImpl,
    private val openWeatherMapRepository: OpenWeatherMapForecastRepositoryImpl,
    private val accuweatherDbRepository: AccuweatherDbRepository,
    private val weatherSettings: WeatherSettingsRepository,
) {

    private var lastUpdateForecast: LocalDateTime? = null

    private suspend fun getAccuweatherForecast(): AccuweatherForecastDto {
        return accuweatherRepository.getWeatherForecast(
            cityKey = weatherSettings.getCityKey(),
            language = weatherSettings.getWeatherLanguage().code,
            apiKeys = weatherSettings.getApiKeys()
        )
    }

    private suspend fun getOpenWeatherMapForecast(): OpenWeatherMapForecastDto {
        return openWeatherMapRepository.getWeatherForecast(
            latitude = weatherSettings.getLatitude(),
            longitude = weatherSettings.getLongitude(),
            language = weatherSettings.getWeatherLanguage().code,
            apiKeys = weatherSettings.getApiKeys(),
            units = weatherSettings.getUnitType()
        )
    }

    private suspend fun getForecastRemote(): Forecast {
        return when (weatherSettings.getConfig().weatherConfig) {
            is WeatherConfigData.Accuweather -> {
                getAccuweatherForecast().let {
                    it.saveToDb(weatherSettings.getCityKey())
                    it.asDomainModel()
                }
            }

            is WeatherConfigData.OpenWeatherMap -> {
                getOpenWeatherMapForecast().let {
                    it.saveToDb(
                        lat = weatherSettings.getLatitude(),
                        lon = weatherSettings.getLongitude()
                    )
                    it.asDomainModel()
                }
            }
        }
    }

    suspend fun getForPeriod(
        startDate: LocalDate,
        endDate: LocalDate
    ): Forecast? {

        val forecastInternal = getForecastInternal(
            startDate,
            endDate
        )

        //Логика обновления данных такая:
        //Если в БД пусто и больше часа ничего не загружалось, либо это в первый раз
        //Если в БД не пусто и прошло больше 2 часов с последнего обновления
        val currentTime = LocalDateTime.now()
        val us0 = lastUpdateForecast == null && (forecastInternal?.dailyForecast?.size ?: 0) < 5
        //Для обновления раз в два часа
        val us1 = lastUpdateForecast?.isEqualsByHour(
            currentTime,
            1
        ) != true
        //Если в БД пусто и данные не запрашивались больше часа
        val us3 =
            forecastInternal?.dailyForecast.isNullOrEmpty() && lastUpdateForecast?.isEqualsByHour(currentTime) != true
        val result = if (us0 || us3 || us1) {
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
        startDate: LocalDate,
        endDate: LocalDate
    ): Forecast? = when (weatherSettings.getConfig().weatherConfig) {
        is WeatherConfigData.Accuweather -> getAccuweatherForecastInternal(
            startDate,
            endDate
        )

        is WeatherConfigData.OpenWeatherMap -> getOpenWeatherMapForecastInternal(
            startDate,
            endDate
        )
    }

    private suspend fun getAccuweatherForecastInternal(
        startDate: LocalDate,
        endDate: LocalDate
    ): Forecast? {
        val headline = accuweatherDbRepository.getHeadline(
            weatherSettings.getCityKey(),
            startDate,
            endDate
        )
        val forecast = accuweatherDbRepository.getForecast(
            weatherSettings.getCityKey(),
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
        startDate: LocalDate,
        endDate: LocalDate
    ): Forecast? {
        //TODO Сделать!!!
        return null
    }

    private suspend fun OpenWeatherMapForecastDto.saveToDb(
        lat: Double,
        lon: Double
    ) {
        //TODO Сделать!!!
    }
}
