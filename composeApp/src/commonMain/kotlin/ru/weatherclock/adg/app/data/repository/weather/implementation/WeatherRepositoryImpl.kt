package ru.weatherclock.adg.app.data.repository.weather.implementation

import ru.weatherclock.adg.app.data.dto.ForecastDto
import ru.weatherclock.adg.app.data.remote.WeatherKtorService
import ru.weatherclock.adg.app.data.repository.weather.WeatherRepository

class WeatherRepositoryImpl(private val ktorService: WeatherKtorService): WeatherRepository {

    override suspend fun getWeatherForecast(
        cityKey: String,
        apiKeys: List<String>,
        language: String
    ): ForecastDto = ktorService.getWeatherForecast(
        cityKey,
        apiKeys,
        language
    )

}
