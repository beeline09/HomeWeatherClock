package ru.weatherclock.adg.app.domain.model.forecast

data class Temperature(

    /**
     * Минимальная температура
     */
    val minimum: UnitInfo,

    /**
     * Максимальная температура
     */
    val maximum: UnitInfo
)