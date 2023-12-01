package ru.weatherclock.adg.app.domain.model.forecast

/**
 * Содержит значение, единицы измерения и название значения
 */
data class UnitInfo(

    /**
     * Значение
     */
    val value: Double,

    /**
     * Единица измерения. Например для температуры: **C**
     */
    val unit: String,

    /**
     * Код единицы измерения
     */
    val unitType: Int = -1,

    /**
     * Описание. Например, **Приятно**
     */
    val phrase: String? = null
)
