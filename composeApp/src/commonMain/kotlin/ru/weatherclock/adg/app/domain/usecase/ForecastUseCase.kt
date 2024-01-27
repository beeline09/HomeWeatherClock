package ru.weatherclock.adg.app.domain.usecase

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.app.data.dto.asDomainModel
import ru.weatherclock.adg.app.data.repository.db.forecast.ForecastDbRepository
import ru.weatherclock.adg.app.data.repository.settings.WeatherSettingsRepository
import ru.weatherclock.adg.app.data.repository.weather.WeatherRepository
import ru.weatherclock.adg.app.data.util.isEqualsByHour
import ru.weatherclock.adg.app.domain.model.Forecast
import ru.weatherclock.adg.app.domain.model.forecast.Detail
import ru.weatherclock.adg.app.domain.model.forecast.DetailType
import ru.weatherclock.adg.app.domain.model.forecast.asDbModel
import ru.weatherclock.adg.app.domain.model.forecast.asDbModelEvapotranspiration
import ru.weatherclock.adg.app.domain.model.forecast.asDbModelIce
import ru.weatherclock.adg.app.domain.model.forecast.asDbModelRain
import ru.weatherclock.adg.app.domain.model.forecast.asDbModelSnow
import ru.weatherclock.adg.app.domain.model.forecast.asDbModelSolarIrradiance
import ru.weatherclock.adg.app.domain.model.forecast.asDbModelTotalLiquid
import ru.weatherclock.adg.app.domain.model.forecast.asDbModelWind
import ru.weatherclock.adg.app.domain.model.forecast.asDbModelWindGust
import ru.weatherclock.adg.app.domain.model.forecast.asDbRealFeelTemperature
import ru.weatherclock.adg.app.domain.model.forecast.asDbRealFeelTemperatureShade
import ru.weatherclock.adg.app.domain.model.forecast.asDbTemperature
import ru.weatherclock.adg.app.domain.model.forecast.asDomainModel
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.db.DailyForecast
import ru.weatherclock.adg.db.ForecastDetail

class ForecastUseCase(
    private val repository: WeatherRepository,
    private val forecastDbRepository: ForecastDbRepository,
    private val weatherSettings: WeatherSettingsRepository,
) {

    private var lastUpdateForecast: LocalDateTime? = null

    suspend fun getForPeriod(
        startDate: LocalDate,
        endDate: LocalDate
    ): Forecast? {
        val forecastKey = weatherSettings.getCityKey()
        val forecast = getForecastInternal(
            forecastKey,
            startDate,
            endDate
        )

        //Логика обновления данных такая:
        //Если в БД пусто и больше часа ничего не загружалось, либо это в первый раз
        //Если в БД не пусто и прошло больше 2 часов с последнего обновления
        val currentTime = LocalDateTime.now()
        //Для обновления раз в два часа
        val us1 = lastUpdateForecast?.isEqualsByHour(
            currentTime,
            1
        ) != true
        //Если в БД пусто и данные не запрашивались больше часа
        val us3 =
            forecast?.dailyForecasts.isNullOrEmpty() && lastUpdateForecast?.isEqualsByHour(currentTime) != true
        val result = if (us3 || us1) {
            val response = try {
                repository.getWeatherForecast(
                    cityKey = forecastKey,
                    apiKeys = weatherSettings.getApiKeys(),
                    language = weatherSettings.getWeatherLanguage().code
                ).asDomainModel().also {
                    lastUpdateForecast = currentTime
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return forecast
            }
            if (response.headline != null || response.dailyForecasts.isNotEmpty()) {
                response.saveToDb(forecastKey = forecastKey)
            }
            getForecastInternal(
                forecastKey,
                startDate,
                endDate
            )
        } else forecast
        return result
    }

    private suspend fun Forecast.saveToDb(forecastKey: String) {
        headline
            ?.asDbModel(forecastKey)
            ?.let { forecastDbRepository.insertHeadline(it) }
        for (f in dailyForecasts) {
            val forecastPid =
                forecastDbRepository.insertDailyForecast(dailyForecast = f.asDbModel(forecastKey))
            if (forecastPid >= 0L) {
                f.sun?.asDbModel(forecastPid = forecastPid)
                    ?.let {
                        forecastDbRepository.insertSun(
                            forecastPid = forecastPid,
                            sun = it
                        )
                    }
                f.moon?.asDbModel(forecastPid = forecastPid)?.let {
                    forecastDbRepository.insertMoon(
                        forecastPid = forecastPid,
                        it
                    )
                }
                f.temperature?.asDbTemperature(forecastPid = forecastPid)?.let {
                    forecastDbRepository.insertTemperature(
                        forecastPid = forecastPid,
                        it
                    )
                }
                f.realFeelTemperature?.asDbRealFeelTemperature(forecastPid = forecastPid)?.let {
                    forecastDbRepository.insertRealFeelTemperature(
                        forecastPid = forecastPid,
                        it
                    )
                }
                f.realFeelTemperatureShade
                    ?.asDbRealFeelTemperatureShade(forecastPid = forecastPid)
                    ?.let {
                        forecastDbRepository.insertRealFeelTemperatureShade(
                            forecastPid = forecastPid,
                            it
                        )
                    }
                f.degreeDaySummary?.asDbModel(forecastPid = forecastPid)?.let {
                    forecastDbRepository.insertDegreeDaySummary(
                        forecastPid = forecastPid,
                        it
                    )
                }
                f.airAndPollen?.let {
                    forecastDbRepository.insertAirAndPollen(airAndPollens = it.map { it.asDbModel(forecastPid = forecastPid) })
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

    private suspend fun Detail.insertDayDetailInternal(
        forecastPid: Long,
        type: DetailType
    ): Long {
        val detailPid = forecastDbRepository.insertDetail(
            forecastPid = forecastPid,
            type = type,
            forecastDetail = asDbModel(forecastPid = forecastPid)
        )
        if (detailPid >= 0L) {
            wind?.let { wind ->
                forecastDbRepository.insertWind(
                    detailPid = detailPid,
                    wind.asDbModelWind(detailPid = detailPid)
                )
            }
            windGust?.let { windGust ->
                forecastDbRepository.insertWindGust(
                    detailPid = detailPid,
                    windGust.asDbModelWindGust(detailPid = detailPid)
                )
            }
            totalLiquid?.let { totalLiquid ->
                forecastDbRepository.insertTotalLiquid(
                    detailPid = detailPid,
                    totalLiquid.asDbModelTotalLiquid(detailPid = detailPid)
                )
            }
            rain?.let { rain ->
                forecastDbRepository.insertRain(
                    detailPid = detailPid,
                    rain.asDbModelRain(detailPid = detailPid)
                )
            }
            snow?.let { snow ->
                forecastDbRepository.insertSnow(
                    detailPid = detailPid,
                    snow.asDbModelSnow(detailPid = detailPid)
                )
            }
            ice?.let { ice ->
                forecastDbRepository.insertIce(
                    detailPid = detailPid,
                    ice.asDbModelIce(detailPid = detailPid)
                )
            }
            evapotranspiration?.let { e ->
                forecastDbRepository.insertEvapotranspiration(
                    detailPid = detailPid,
                    e.asDbModelEvapotranspiration(detailPid = detailPid)
                )
            }
            solarIrradiance?.let { si ->
                forecastDbRepository.insertSolarIrradiance(
                    detailPid = detailPid,
                    si.asDbModelSolarIrradiance(detailPid = detailPid)
                )
            }
        }
        return detailPid
    }

    private suspend fun getForecastInternal(
        forecastKey: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Forecast? {
        val headline = forecastDbRepository
            .getHeadline(
                forecastKey,
                startDate,
                endDate
            )
            ?.asDomainModel()
        val forecast = forecastDbRepository.getForecast(
            forecastKey,
            startDate,
            endDate
        ).map { it.toDomainModel() }
        return Forecast(
            headline = headline,
            dailyForecasts = forecast
        ).takeIf { headline != null || forecast.isNotEmpty() }
    }

    private suspend fun ForecastDetail.toDomainModel(): Detail {
        return asDomainModel(
            evapotranspiration = forecastDbRepository
                .getEvapotranspiration(byDetailPid = pid)
                ?.asDomainModel(),
            solarIrradiance = forecastDbRepository
                .getSolarIrradiance(byDetailPid = pid)
                ?.asDomainModel(),
            wind = forecastDbRepository.getWindGust(byDetailPid = pid)?.asDomainModel(),
            windGust = forecastDbRepository.getWindGust(byDetailPid = pid)?.asDomainModel(),
            totalLiquid = forecastDbRepository.getTotalLiquid(byDetailPid = pid)?.asDomainModel(),
            snow = forecastDbRepository.getSnow(byDetailPid = pid)?.asDomainModel(),
            rain = forecastDbRepository.getRain(byDetailPid = pid)?.asDomainModel(),
            ice = forecastDbRepository.getIce(byDetailPid = pid)?.asDomainModel()
        )
    }

    private suspend fun DailyForecast.toDomainModel(): ru.weatherclock.adg.app.domain.model.forecast.DailyForecast {
        return asDomainModel(
            airAndPollen = forecastDbRepository
                .getAirAndPollen(byForecastPid = pid)
                .map { it.asDomainModel() },
            degreeDaySummary = forecastDbRepository
                .getDegreeDaySummary(byForecastPid = pid)
                ?.asDomainModel(),
            temperature = forecastDbRepository
                .getTemperature(byForecastPid = pid)
                ?.asDomainModel(),
            realFeelTemperature = forecastDbRepository
                .getRealFeelTemperature(byForecastPid = pid)
                ?.asDomainModel(),
            realFeelTemperatureShade = forecastDbRepository
                .getRealFeelTemperatureShade(byForecastPid = pid)
                ?.asDomainModel(),
            day = forecastDbRepository
                .getDetail(
                    byForecastPid = pid,
                    type = DetailType.DAY
                )
                ?.toDomainModel(),
            night = forecastDbRepository.getDetail(
                byForecastPid = pid,
                type = DetailType.NIGHT
            )?.toDomainModel(),
            moon = forecastDbRepository.getMoon(byForecastPid = pid)?.asDomainModel(),
            sun = forecastDbRepository.getSun(byForecastPid = pid)?.asDomainModel()
        )
    }
}
