package ru.weatherclock.adg.app.data.repository

import ru.weatherclock.adg.app.data.dto.ForecastDto

abstract class AbstractRepository {

    abstract suspend fun getWeatherForecast(forecastKey: String): ForecastDto
}
