package ru.weatherclock.adg.app.data.repository

import ru.weatherclock.adg.app.data.dto.ForecastDto

abstract class WeatherRepository {

    abstract suspend fun getWeatherForecast(forecastKey: String): ForecastDto
}
