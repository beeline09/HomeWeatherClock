package ru.weatherclock.adg.app.data.remote

import ru.weatherclock.adg.app.data.dto.ForecastDto

abstract class WeatherKtorService {

    abstract suspend fun getWeatherForecast(
        cityKey: String,
        apiKeys: List<String>,
        language: String
    ): ForecastDto
}
