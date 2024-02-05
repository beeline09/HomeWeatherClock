package ru.weatherclock.adg.app.data.repository.db.forecast.implementation

import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.app.data.repository.db.forecast.OpenWeatherMapDbRepository
import ru.weatherclock.adg.app.data.util.toDbFormat
import ru.weatherclock.adg.db.OpenWeatherMap.ForecastItem
import ru.weatherclock.adg.db.OpenWeatherMap.MainInfo
import ru.weatherclock.adg.db.OpenWeatherMap.Rain
import ru.weatherclock.adg.db.OpenWeatherMap.Snow
import ru.weatherclock.adg.db.OpenWeatherMap.WeatherIcon
import ru.weatherclock.adg.db.OpenWeatherMap.Wind
import ru.weatherclock.adg.db.OpenWeatherMapDb

class OpenWeatherMapDbRepositoryImpl(private val database: OpenWeatherMapDb):
    OpenWeatherMapDbRepository {

    private fun ForecastItem.insertForecast(): Long {
        database.forecastItemQueries.insert(
            pid = pid.takeIf { it > 0 },
            latitude = latitude,
            longitude = longitude,
            part_of_day = part_of_day,
            clouds = clouds,
            visibility = visibility,
            date_time = date_time,
            pop = pop
        )
        return database.forecastItemQueries.lastPid().executeAsOne().MAX ?: 0L
    }

    override suspend fun insertDailyForecast(
        dailyForecast: ForecastItem
    ): Long {
        return dailyForecast.insertForecast()
    }

    override suspend fun insertDailyForecast(dailyForecasts: List<ForecastItem>): List<Long> {
        return database.transactionWithResult {
            dailyForecasts.map {
                it.insertForecast()
            }
        }
    }

    override suspend fun insertMainInfo(info: MainInfo) {
        database.transaction {
            database.mainInfoQueries.insert(info)
        }
    }

    override suspend fun insertWeatherIcon(icon: WeatherIcon) {
        database.transaction {
            database.weatherIconQueries.insert(icon)
        }
    }

    override suspend fun insertWind(wind: Wind) {
        database.transaction {
            database.windQueries.insert(wind)
        }
    }

    override suspend fun insertRain(rain: Rain) {
        database.transaction {
            database.rainQueries.insert(rain)
        }
    }

    override suspend fun insertSnow(snow: Snow) {
        database.transaction {
            database.snowQueries.insert(snow)
        }
    }

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<ForecastItem> {
        return database.transactionWithResult {
            database.forecastItemQueries.getByDateRange(
                date_time = startDate.toDbFormat(),
                date_time_ = endDate.toDbFormat(),
                latitude = latitude,
                longitude = longitude
            ).executeAsList()
        }
    }

    override suspend fun getMainInfo(
        byForecastPid: Long
    ): MainInfo {
        return database.transactionWithResult {
            database.mainInfoQueries.getByForecastPid(forecast_pid = byForecastPid).executeAsOne()
        }
    }

    override suspend fun getWeatherIcon(byForecastPid: Long): List<WeatherIcon> {
        return database.transactionWithResult {
            database.weatherIconQueries.getByForecastPid(
                forecast_pid = byForecastPid
            ).executeAsList()
        }
    }

}