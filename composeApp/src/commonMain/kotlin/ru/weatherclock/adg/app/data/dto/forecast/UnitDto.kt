package ru.weatherclock.adg.app.data.dto.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.forecast.UnitInfo

/**
 * Содержит значение, единицы измерения и название значения
 */
@Serializable
data class UnitDto(

    /**
     * Значение
     */
    @SerialName("Value")
    val value: Double,

    /**
     * Единица измерения. Например для температуры: **C**
     */
    @SerialName("Unit")
    val unit: String,

    /**
     * Код единицы измерения
     */
    @SerialName("UnitType")
    val unitType: Int = -1,

    /**
     * Описание. Например, **Приятно**
     */
    @SerialName("Phrase")
    val phrase: String? = null
)

fun UnitDto.asDomainModel(): UnitInfo = UnitInfo(
    value = value,
    unit = unit,
    unitType = unitType,
    phrase = phrase
)
