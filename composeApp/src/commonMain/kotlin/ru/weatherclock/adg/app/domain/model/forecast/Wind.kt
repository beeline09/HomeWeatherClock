package ru.weatherclock.adg.app.domain.model.forecast

data class Wind(
    /**
     * Скорость ветра
     */
    val speed: UnitInfo,

    /**
     * Направлени ветра
     */
    val direction: Direction
)