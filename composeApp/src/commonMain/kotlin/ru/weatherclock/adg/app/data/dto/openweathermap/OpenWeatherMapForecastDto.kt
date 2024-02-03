package ru.weatherclock.adg.app.data.dto.openweathermap

import kotlinx.serialization.Serializable

@Serializable
data class OpenWeatherMapForecastDto(
    val cnt: Int = 0,
    val message: Int = 0,
    val cod: String? = null,

    )
