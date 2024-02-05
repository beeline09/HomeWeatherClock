package ru.weatherclock.adg.app.data.repository.db.forecast

import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.db.OpenWeatherMap.ForecastItem
import ru.weatherclock.adg.db.OpenWeatherMap.MainInfo
import ru.weatherclock.adg.db.OpenWeatherMap.Rain
import ru.weatherclock.adg.db.OpenWeatherMap.Snow
import ru.weatherclock.adg.db.OpenWeatherMap.WeatherIcon
import ru.weatherclock.adg.db.OpenWeatherMap.Wind

interface OpenWeatherMapDbRepository {

    suspend fun insertDailyForecast(dailyForecast: ForecastItem): Long
    suspend fun insertDailyForecast(dailyForecasts: List<ForecastItem>): List<Long>
    suspend fun insertMainInfo(info: MainInfo)
    suspend fun insertWeatherIcon(icon: WeatherIcon)
    suspend fun insertWind(wind: Wind)
    suspend fun insertRain(rain: Rain)
    suspend fun insertSnow(snow: Snow)

    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<ForecastItem>

    suspend fun getMainInfo(byForecastPid: Long): MainInfo
    suspend fun getWeatherIcon(byForecastPid: Long): List<WeatherIcon>
}