package ru.weatherclock.adg.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherSettings(
    val region: Int = 77,
    val isRussia: Boolean = true,

    )
