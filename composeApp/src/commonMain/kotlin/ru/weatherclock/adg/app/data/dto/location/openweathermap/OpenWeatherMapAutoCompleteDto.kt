package ru.weatherclock.adg.app.data.dto.location.openweathermap

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenWeatherMapAutoCompleteDto(
    @SerialName("name")
    val name: String = "",
    @SerialName("local_names")
    val localName: Map<String, String> = emptyMap(),
    @SerialName("lat")
    val latitude: Double = 0.0,
    @SerialName("lon")
    val longitude: Double = 0.0,
    @SerialName("country")
    val country: String = "",
    @SerialName("state")
    val state: String? = null
)
