package ru.weatherclock.adg.app.data.dto.forecast.openweathermap.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("main")
    val main: String = "",

    @SerialName("description")
    val description: String? = null,

    @SerialName("icon")
    val icon: String = ""
)
