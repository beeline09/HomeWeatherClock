package ru.weatherclock.adg.app.domain.model.forecast

data class Sun(
    /**
     * Восход
     */
    val rise: Long,

    /**
     * Закат
     */
    val set: Long
)
