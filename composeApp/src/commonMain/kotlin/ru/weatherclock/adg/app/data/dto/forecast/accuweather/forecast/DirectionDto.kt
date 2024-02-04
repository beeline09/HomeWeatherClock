package ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DirectionDto(

    /**
     * Направление ветра в азимутальных градусах (например, 180° указывает на ветер, дующий с юга).
     */
    @SerialName("Degrees")
    val degrees: Double = 0.0,

    /**
     * Краткое локализованное название направления. Например, **ЮЮВ**
     */
    @SerialName("Localized")
    val localized: String = "",

    /**
     * Краткое название направления на английском. Например, **SSE**
     */
    @SerialName("English")
    val english: String = "",
)