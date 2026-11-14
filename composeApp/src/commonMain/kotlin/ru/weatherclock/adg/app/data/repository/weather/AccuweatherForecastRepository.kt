package ru.weatherclock.adg.app.data.repository.weather

import ru.weatherclock.adg.app.data.dto.forecast.accuweather.AccuweatherForecastDto

/** Абстракция источника прогноза Accuweather (DIP: use case не знает Impl) */
interface AccuweatherForecastRepository {

    suspend fun getWeatherForecast(
        cityKey: String,
        apiKeys: List<String>,
        language: String
    ): AccuweatherForecastDto
}
