package ru.weatherclock.adg.app.domain.model.forecast

data class Moon(
    /**
     * Восход
     */
    val rise: Long,

    /**
     * Закат
     */
    val set: Long,

    /**
     * Фаза
     */
    val phase: String,

    /**
     * Число дней после новолуния.
     */
    val age: Int
)
