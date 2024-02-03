package ru.weatherclock.adg.app.data.repository.weather

import ru.weatherclock.adg.app.data.dto.accuweather.AccuweatherForecastDto

interface WeatherRepository {

    suspend fun getWeatherForecast(
        cityKey: String,
        apiKeys: List<String>,
        language: String
    ): AccuweatherForecastDto
}
