package ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.db.Accuweather.Evapotranspiration
import ru.weatherclock.adg.db.Accuweather.Ice
import ru.weatherclock.adg.db.Accuweather.Rain
import ru.weatherclock.adg.db.Accuweather.Snow
import ru.weatherclock.adg.db.Accuweather.SolarIrradiance
import ru.weatherclock.adg.db.Accuweather.TotalLiquid

/**
 * Содержит значение, единицы измерения и название значения
 */
@Serializable
data class UnitDto(

    /**
     * Значение
     */
    @SerialName("Value")
    val value: Double = 0.0,

    /**
     * Единица измерения. Например для температуры: **C**
     */
    @SerialName("Unit")
    val unit: String = "",

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

fun UnitDto.asAccuweatherDbModelTotalLiquid(detailPid: Long): TotalLiquid {
    return TotalLiquid(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}

fun UnitDto.asAccuweatherDbModelRain(detailPid: Long): Rain {
    return Rain(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}

fun UnitDto.asAccuweatherDbModelSnow(detailPid: Long): Snow {
    return Snow(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}

fun UnitDto.asAccuweatherDbModelIce(detailPid: Long): Ice {
    return Ice(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}

fun UnitDto.asAccuweatherDbModelEvapotranspiration(detailPid: Long): Evapotranspiration {
    return Evapotranspiration(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}

fun UnitDto.asAccuweatherDbModelSolarIrradiance(detailPid: Long): SolarIrradiance {
    return SolarIrradiance(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}
