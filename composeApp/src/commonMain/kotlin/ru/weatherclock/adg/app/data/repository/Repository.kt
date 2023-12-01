package ru.weatherclock.adg.app.data.repository

import ru.weatherclock.adg.app.data.dto.ForecastDto
import ru.weatherclock.adg.app.data.remote.AbstractKtorService

class Repository(private val ktorService: AbstractKtorService): AbstractRepository() {

    override suspend fun getWeatherForecast(forecastKey: String): ForecastDto =
        ktorService.getWeatherForecast(forecastKey)

}
