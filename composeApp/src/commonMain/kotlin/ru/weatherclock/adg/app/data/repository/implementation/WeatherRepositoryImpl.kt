package ru.weatherclock.adg.app.data.repository.implementation

import ru.weatherclock.adg.app.data.dto.ForecastDto
import ru.weatherclock.adg.app.data.remote.WeatherKtorService
import ru.weatherclock.adg.app.data.repository.WeatherRepository

class WeatherRepositoryImpl(private val ktorService: WeatherKtorService): WeatherRepository() {

    override suspend fun getWeatherForecast(forecastKey: String): ForecastDto =
        ktorService.getWeatherForecast(forecastKey)

}
