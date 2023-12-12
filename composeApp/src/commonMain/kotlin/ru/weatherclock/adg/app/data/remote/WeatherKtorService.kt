package ru.weatherclock.adg.app.data.remote

import ru.weatherclock.adg.app.data.dto.ForecastDto

abstract class WeatherKtorService {

    abstract suspend fun getWeatherForecast(forecastKey: String): ForecastDto
}
