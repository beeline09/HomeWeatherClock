package ru.weatherclock.adg.app.data.repository.weather

import ru.weatherclock.adg.app.data.WeatherUnits
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.OpenWeatherMapForecastDto

/** Абстракция источника прогноза OpenWeatherMap (DIP: use case не знает Impl) */
interface OpenWeatherMapForecastRepository {

    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        units: WeatherUnits,
        apiKeys: List<String>,
        language: String
    ): OpenWeatherMapForecastDto
}
