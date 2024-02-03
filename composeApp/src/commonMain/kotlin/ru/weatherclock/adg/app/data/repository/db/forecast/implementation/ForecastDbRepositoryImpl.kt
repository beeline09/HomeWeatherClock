package ru.weatherclock.adg.app.data.repository.db.forecast.implementation

import kotlinx.datetime.LocalDate
import ru.weatherclock.adg.app.data.repository.db.forecast.ForecastDbRepository
import ru.weatherclock.adg.app.data.util.toDbFormat
import ru.weatherclock.adg.app.domain.model.forecast.DetailType
import ru.weatherclock.adg.db.Accuweather.AirAndPollen
import ru.weatherclock.adg.db.Accuweather.DailyForecast
import ru.weatherclock.adg.db.Accuweather.DegreeDaySummary
import ru.weatherclock.adg.db.Accuweather.Evapotranspiration
import ru.weatherclock.adg.db.Accuweather.ForecastDetail
import ru.weatherclock.adg.db.Accuweather.ForecastHeadline
import ru.weatherclock.adg.db.Accuweather.Ice
import ru.weatherclock.adg.db.Accuweather.Moon
import ru.weatherclock.adg.db.Accuweather.Rain
import ru.weatherclock.adg.db.Accuweather.RealFeelTemperature
import ru.weatherclock.adg.db.Accuweather.RealFeelTemperatureShade
import ru.weatherclock.adg.db.Accuweather.Snow
import ru.weatherclock.adg.db.Accuweather.SolarIrradiance
import ru.weatherclock.adg.db.Accuweather.Sun
import ru.weatherclock.adg.db.Accuweather.Temperature
import ru.weatherclock.adg.db.Accuweather.TotalLiquid
import ru.weatherclock.adg.db.Accuweather.Wind
import ru.weatherclock.adg.db.Accuweather.WindGust
import ru.weatherclock.adg.db.AccuweatherDb

class ForecastDbRepositoryImpl(private val database: AccuweatherDb): ForecastDbRepository {

    override suspend fun insertHeadline(headline: ForecastHeadline) {
        database.transaction {
            database.forecastHeadlineQueries.insert(
                pid = if (headline.pid >= 0L) headline.pid else null,
                forecast_key = headline.forecast_key,
                start_date = headline.start_date,
                end_date = headline.end_date,
                category = headline.category,
                severity = headline.severity,
                text = headline.text
            )
        }
    }

    override suspend fun insertDailyForecast(dailyForecast: DailyForecast): Long {
        return database.transactionWithResult {
            dailyForecast.insertForecast()
        }
    }

    override suspend fun insertDailyForecast(dailyForecasts: List<DailyForecast>): List<Long> {
        return database.transactionWithResult {
            dailyForecasts.map { it.insertForecast() }
        }
    }

    private fun DailyForecast.insertForecast(): Long {
        database.dayliForecastQueries.insert(
            pid = pid.takeIf { it >= 0L },
            date = date,
            hours_of_sun = hours_of_sun,
            forecast_key = forecast_key
        )
        return database.dayliForecastQueries.lastPid().executeAsOne().MAX ?: 0L
    }

    private fun insertAirAndPollenInternal(airAndPollen: AirAndPollen): Long {
        database.airAndPollenQueries.insert(
            pid = if (airAndPollen.pid >= 0L) airAndPollen.pid else null,
            forecast_pid = airAndPollen.forecast_pid,
            category = airAndPollen.category,
            type = airAndPollen.type,
            categoryValue = airAndPollen.categoryValue,
            value_ = airAndPollen.value_,
            name = airAndPollen.name
        )
        return database.airAndPollenQueries.lastPid().executeAsOneOrNull()?.MAX ?: -1L
    }

    override suspend fun insertAirAndPollen(airAndPollen: AirAndPollen): Long {
        return database.transactionWithResult {
            insertAirAndPollenInternal(airAndPollen)
        }
    }

    override suspend fun insertAirAndPollen(airAndPollens: List<AirAndPollen>): List<Long> {
        return database.transactionWithResult {
            airAndPollens.map { insertAirAndPollenInternal(it) }
        }
    }

    override suspend fun insertDegreeDaySummary(
        forecastPid: Long,
        degreeDaySummary: DegreeDaySummary
    ): Long {
        return database.transactionWithResult {
            database.degreeDaySummaryQueries.insert(
                pid = if (degreeDaySummary.pid >= 0L) degreeDaySummary.pid else null,
                forecast_pid = forecastPid,
                cooling_value = degreeDaySummary.cooling_value,
                cooling_unit_type = degreeDaySummary.cooling_unit_type,
                cooling_unit = degreeDaySummary.cooling_unit,
                cooling_phrase = degreeDaySummary.cooling_phrase,
                heating_value = degreeDaySummary.heating_value,
                heating_unit_type = degreeDaySummary.heating_unit_type,
                heating_unit = degreeDaySummary.heating_unit,
                heating_phrase = degreeDaySummary.heating_phrase,
            )
            database.degreeDaySummaryQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertTemperature(
        forecastPid: Long,
        temperature: Temperature
    ): Long {
        return database.transactionWithResult {
            database.temperatureQueries.insert(
                pid = if (temperature.pid >= 0L) temperature.pid else null,
                max_phrase = temperature.max_phrase,
                max_unit = temperature.max_unit,
                max_unit_type = temperature.max_unit_type,
                max_value = temperature.max_value,
                min_unit_type = temperature.min_unit_type,
                min_unit = temperature.min_unit,
                min_phrase = temperature.min_phrase,
                min_value = temperature.min_value,
                forecast_pid = forecastPid
            )
            database.temperatureQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertRealFeelTemperature(
        forecastPid: Long,
        realFeelTemperature: RealFeelTemperature
    ): Long {
        return database.transactionWithResult {
            database.realFeelTemperatureQueries.insert(
                pid = realFeelTemperature.pid.takeIf { it >= 0L },
                forecast_pid = forecastPid,
                min_value = realFeelTemperature.min_value,
                min_phrase = realFeelTemperature.min_phrase,
                min_unit = realFeelTemperature.min_unit,
                min_unit_type = realFeelTemperature.min_unit_type,
                max_value = realFeelTemperature.max_value,
                max_unit_type = realFeelTemperature.max_unit_type,
                max_unit = realFeelTemperature.max_unit,
                max_phrase = realFeelTemperature.max_phrase
            )
            database.realFeelTemperatureQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertRealFeelTemperatureShade(
        forecastPid: Long,
        realFeelTemperatureShade: RealFeelTemperatureShade
    ): Long {
        return database.transactionWithResult {
            database.realFeelTemperatureShadeQueries.insert(
                pid = realFeelTemperatureShade.pid.takeIf { it >= 0L },
                forecast_pid = forecastPid,
                min_value = realFeelTemperatureShade.min_value,
                min_phrase = realFeelTemperatureShade.min_phrase,
                min_unit = realFeelTemperatureShade.min_unit,
                min_unit_type = realFeelTemperatureShade.min_unit_type,
                max_value = realFeelTemperatureShade.max_value,
                max_unit_type = realFeelTemperatureShade.max_unit_type,
                max_unit = realFeelTemperatureShade.max_unit,
                max_phrase = realFeelTemperatureShade.max_phrase
            )
            database.realFeelTemperatureQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertDetail(
        forecastPid: Long,
        forecastDetail: ForecastDetail,
        type: DetailType
    ): Long {
        return database.transactionWithResult {
            database.forecastDetailQueries.insert(
                forecast_pid = forecastPid,
                pid = forecastDetail.pid.takeIf { it >= 0L },
                is_night = forecastDetail.is_night,
                thunderstorm_probability = forecastDetail.thunderstorm_probability,
                long_phrase = forecastDetail.long_phrase,
                rain_probability = forecastDetail.rain_probability,
                snow_probability = forecastDetail.snow_probability,
                ice_probability = forecastDetail.ice_probability,
                short_phrase = forecastDetail.short_phrase,
                hours_of_snow = forecastDetail.hours_of_snow,
                hours_of_rain = forecastDetail.hours_of_rain,
                hours_of_ice = forecastDetail.hours_of_ice,
                cloud_cover = forecastDetail.cloud_cover,
                hours_of_precipitation = forecastDetail.hours_of_precipitation,
                precipitation_type = forecastDetail.precipitation_type,
                precipitation_probability = forecastDetail.precipitation_probability,
                precipitation_intensity = forecastDetail.precipitation_intensity,
                has_precipitation = forecastDetail.has_precipitation,
                icon_phrase = forecastDetail.icon_phrase,
                icon = forecastDetail.icon
            )
            database.forecastDetailQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertEvapotranspiration(
        detailPid: Long,
        evapotranspiration: Evapotranspiration
    ): Long {
        return database.transactionWithResult {
            database.evapotranspirationQueries.insert(
                pid = evapotranspiration.pid.takeIf { it >= 0L },
                value_ = evapotranspiration.value_,
                unit = evapotranspiration.unit,
                unit_type = evapotranspiration.unit_type,
                phrase = evapotranspiration.phrase,
                detail_pid = detailPid
            )
            database.evapotranspirationQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertSolarIrradiance(
        detailPid: Long,
        solarIrradiance: SolarIrradiance
    ): Long {
        return database.transactionWithResult {
            database.solarIrradianceQueries.insert(
                pid = solarIrradiance.pid.takeIf { it >= 0L },
                detail_pid = detailPid,
                value_ = solarIrradiance.value_,
                unit = solarIrradiance.unit,
                unit_type = solarIrradiance.unit_type,
                phrase = solarIrradiance.phrase,
            )
            database.solarIrradianceQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertWind(
        detailPid: Long,
        wind: Wind
    ): Long {
        return database.transactionWithResult {
            database.windQueries.insert(
                pid = wind.pid.takeIf { it >= 0L },
                detail_pid = detailPid,
                direction_localized = wind.direction_localized,
                direction_english = wind.direction_english,
                direction_degrees = wind.direction_degrees,
                speed_value = wind.speed_value,
                speed_unit_type = wind.speed_unit_type,
                speed_unit = wind.speed_unit,
                speed_phrase = wind.speed_phrase
            )
            database.windQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertWindGust(
        detailPid: Long,
        windGust: WindGust
    ): Long {
        return database.transactionWithResult {
            database.windGustQueries.insert(
                pid = windGust.pid.takeIf { it >= 0L },
                detail_pid = detailPid,
                direction_localized = windGust.direction_localized,
                direction_english = windGust.direction_english,
                direction_degrees = windGust.direction_degrees,
                speed_value = windGust.speed_value,
                speed_unit_type = windGust.speed_unit_type,
                speed_unit = windGust.speed_unit,
                speed_phrase = windGust.speed_phrase
            )
            database.windGustQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertTotalLiquid(
        detailPid: Long,
        totalLiquid: TotalLiquid
    ): Long {
        return database.transactionWithResult {
            database.totalLiquidQueries.insert(
                pid = totalLiquid.pid.takeIf { it >= 0L },
                detail_pid = detailPid,
                value_ = totalLiquid.value_,
                unit = totalLiquid.unit,
                unit_type = totalLiquid.unit_type,
                phrase = totalLiquid.phrase,
            )
            database.totalLiquidQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertSnow(
        detailPid: Long,
        snow: Snow
    ): Long {
        return database.transactionWithResult {
            database.snowQueries.insert(
                pid = snow.pid.takeIf { it >= 0L },
                detail_pid = detailPid,
                value_ = snow.value_,
                unit = snow.unit,
                unit_type = snow.unit_type,
                phrase = snow.phrase,
            )
            database.snowQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertRain(
        detailPid: Long,
        rain: Rain
    ): Long {
        return database.transactionWithResult {
            database.rainQueries.insert(
                pid = rain.pid.takeIf { it >= 0L },
                detail_pid = detailPid,
                value_ = rain.value_,
                unit = rain.unit,
                unit_type = rain.unit_type,
                phrase = rain.phrase,
            )
            database.rainQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertIce(
        detailPid: Long,
        ice: Ice
    ): Long {
        return database.transactionWithResult {
            database.iceQueries.insert(
                pid = ice.pid.takeIf { it >= 0L },
                detail_pid = detailPid,
                value_ = ice.value_,
                unit = ice.unit,
                unit_type = ice.unit_type,
                phrase = ice.phrase,
            )
            database.iceQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertMoon(
        forecastPid: Long,
        moon: Moon
    ): Long {
        return database.transactionWithResult {
            database.moonQueries.insert(
                pid = moon.pid.takeIf { it >= 0L },
                forecast_pid = forecastPid,
                age = moon.age,
                set_date_time = moon.set_date_time,
                rise_date_time = moon.rise_date_time,
                phase = moon.phase
            )
            database.moonQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun insertSun(
        forecastPid: Long,
        sun: ru.weatherclock.adg.db.Accuweather.Sun
    ): Long {
        return database.transactionWithResult {
            database.sunQueries.insert(
                pid = sun.pid.takeIf { it >= 0L },
                forecast_pid = forecastPid,
                set_date_time = sun.set_date_time,
                rise_date_time = sun.rise_date_time
            )
            database.sunQueries.lastPid().executeAsOne().MAX ?: 0L
        }
    }

    override suspend fun getHeadline(
        forecastKey: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): ForecastHeadline? {
        return database.transactionWithResult {
            database.forecastHeadlineQueries.selectByDateRange(
                start_date = startDate.toDbFormat(),
                end_date = endDate.toDbFormat(),
                forecast_key = forecastKey
            ).executeAsOneOrNull()
        }
    }

    override suspend fun getForecast(
        forecastKey: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailyForecast> = database.transactionWithResult {
        database.dayliForecastQueries.selectByDateRange(
            date = startDate.toDbFormat(),
            date_ = endDate.toDbFormat(),
            forecast_key = forecastKey
        ).executeAsList()
    }

    override suspend fun getAirAndPollen(byForecastPid: Long): List<AirAndPollen> =
        database.transactionWithResult {
            database.airAndPollenQueries
                .getByForecastPid(forecast_pid = byForecastPid)
                .executeAsList()
        }

    override suspend fun getDegreeDaySummary(byForecastPid: Long): DegreeDaySummary? =
        database.transactionWithResult {
            database.degreeDaySummaryQueries
                .getByForecastPid(forecast_pid = byForecastPid)
                .executeAsOneOrNull()
        }

    override suspend fun getTemperature(byForecastPid: Long): Temperature? =
        database.transactionWithResult {
            database.temperatureQueries
                .getByForecastPid(forecast_pid = byForecastPid)
                .executeAsOneOrNull()
        }

    override suspend fun getRealFeelTemperature(byForecastPid: Long): RealFeelTemperature? =
        database.transactionWithResult {
            database.realFeelTemperatureQueries
                .getByForecastPid(forecast_pid = byForecastPid)
                .executeAsOneOrNull()
        }

    override suspend fun getRealFeelTemperatureShade(byForecastPid: Long): RealFeelTemperatureShade? =
        database.transactionWithResult {
            database.realFeelTemperatureShadeQueries
                .getByForecastPid(forecast_pid = byForecastPid)
                .executeAsOneOrNull()
        }

    override suspend fun getDetail(
        byForecastPid: Long,
        type: DetailType
    ): ForecastDetail? = database.transactionWithResult {
        database.forecastDetailQueries.getByForecastPid(
            forecast_pid = byForecastPid,
            is_night = type == DetailType.NIGHT
        ).executeAsOneOrNull()
    }

    override suspend fun getEvapotranspiration(byDetailPid: Long): Evapotranspiration? =
        database.transactionWithResult {
            database.evapotranspirationQueries
                .getByDetailPid(detail_pid = byDetailPid)
                .executeAsOneOrNull()
        }

    override suspend fun getSolarIrradiance(byDetailPid: Long): SolarIrradiance? =
        database.transactionWithResult {
            database.solarIrradianceQueries
                .getByDetailPid(detail_pid = byDetailPid)
                .executeAsOneOrNull()
        }

    override suspend fun getWind(byDetailPid: Long): Wind? = database.transactionWithResult {
        database.windQueries.getByDetailPid(detail_pid = byDetailPid).executeAsOneOrNull()
    }

    override suspend fun getWindGust(byDetailPid: Long): WindGust? =
        database.transactionWithResult {
            database.windGustQueries.getByDetailPid(detail_pid = byDetailPid).executeAsOneOrNull()
        }

    override suspend fun getTotalLiquid(byDetailPid: Long): TotalLiquid? =
        database.transactionWithResult {
            database.totalLiquidQueries
                .getByDetailPid(detail_pid = byDetailPid)
                .executeAsOneOrNull()
        }

    override suspend fun getSnow(byDetailPid: Long): Snow? = database.transactionWithResult {
        database.snowQueries.getByDetailPid(detail_pid = byDetailPid).executeAsOneOrNull()
    }

    override suspend fun getRain(byDetailPid: Long): Rain? = database.transactionWithResult {
        database.rainQueries.getByDetailPid(detail_pid = byDetailPid).executeAsOneOrNull()
    }

    override suspend fun getIce(byDetailPid: Long): Ice? = database.transactionWithResult {
        database.iceQueries.getByDetailPid(detail_pid = byDetailPid).executeAsOneOrNull()
    }

    override suspend fun getMoon(byForecastPid: Long): Moon? = database.transactionWithResult {
        database.moonQueries.getByForecastPid(forecast_pid = byForecastPid).executeAsOneOrNull()
    }

    override suspend fun getSun(byForecastPid: Long): Sun? = database.transactionWithResult {
        database.sunQueries.getByForecastPid(forecast_pid = byForecastPid).executeAsOneOrNull()
    }

}