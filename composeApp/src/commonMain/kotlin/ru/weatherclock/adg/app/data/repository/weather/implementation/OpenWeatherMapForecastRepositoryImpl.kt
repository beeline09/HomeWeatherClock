package ru.weatherclock.adg.app.data.repository.weather.implementation

import ru.weatherclock.adg.app.data.WeatherUnits
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.OpenWeatherMapForecastDto
import ru.weatherclock.adg.app.data.remote.service.implementation.OpenWeatherMapKtorServiceImpl
import ru.weatherclock.adg.app.data.repository.weather.WeatherForecastRepository

class OpenWeatherMapForecastRepositoryImpl(private val ktorService: OpenWeatherMapKtorServiceImpl):
    WeatherForecastRepository {

    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        units: WeatherUnits,
        apiKeys: List<String>,
        language: String
    ): OpenWeatherMapForecastDto = ktorService.getWeatherForecast(
        latitude,
        longitude,
        units,
        apiKeys,
        language
    )

}
