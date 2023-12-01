package ru.weatherclock.adg.app.domain.model.forecast

data class Direction(

    /**
     * Направление ветра в азимутальных градусах (например, 180° указывает на ветер, дующий с юга).
     */
    val degrees: Double,

    /**
     * Краткое локализованное название направления. Например, **ЮЮВ**
     */
    val localized: String,

    /**
     * Краткое название направления на английском. Например, **SSE**
     */
    val english: String,
)