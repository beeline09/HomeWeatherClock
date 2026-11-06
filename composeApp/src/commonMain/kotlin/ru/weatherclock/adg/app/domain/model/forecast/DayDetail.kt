package ru.weatherclock.adg.app.domain.model.forecast

data class DayDetail(
    val temperature: Double = 0.0,
    val icon: String = "",
    val iconPhrase: String? = null
)
