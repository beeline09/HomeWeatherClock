package ru.weatherclock.adg.app.data.dto.forecast.openweathermap

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.dto.forecast.ForecastDto
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.detail.ForecastItemDto

@Serializable
data class OpenWeatherMapForecastDto(
    @SerialName("cnt")
    val count: Int = 0,
    @SerialName("message")
    val message: Int = 0,
    @SerialName("cod")
    val code: String? = null,
    @SerialName("list")
    val items: List<ForecastItemDto> = emptyList()
): ForecastDto
