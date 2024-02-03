package ru.weatherclock.adg.app.domain.model.forecast

import ru.weatherclock.adg.db.Accuweather.Evapotranspiration
import ru.weatherclock.adg.db.Accuweather.Ice
import ru.weatherclock.adg.db.Accuweather.Rain
import ru.weatherclock.adg.db.Accuweather.Snow
import ru.weatherclock.adg.db.Accuweather.SolarIrradiance
import ru.weatherclock.adg.db.Accuweather.TotalLiquid

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

fun Evapotranspiration.asDomainModel(): UnitInfo {
    return UnitInfo(
        value = value_,
        unit = unit,
        unitType = unit_type,
        phrase = phrase
    )
}

fun SolarIrradiance.asDomainModel(): UnitInfo {
    return UnitInfo(
        value = value_,
        unit = unit,
        unitType = unit_type,
        phrase = phrase
    )
}

fun TotalLiquid.asDomainModel(): UnitInfo {
    return UnitInfo(
        value = value_,
        unit = unit,
        unitType = unit_type,
        phrase = phrase
    )
}

fun Snow.asDomainModel(): UnitInfo {
    return UnitInfo(
        value = value_,
        unit = unit,
        unitType = unit_type,
        phrase = phrase
    )
}

fun Rain.asDomainModel(): UnitInfo {
    return UnitInfo(
        value = value_,
        unit = unit,
        unitType = unit_type,
        phrase = phrase
    )
}

fun Ice.asDomainModel(): UnitInfo {
    return UnitInfo(
        value = value_,
        unit = unit,
        unitType = unit_type,
        phrase = phrase
    )
}

fun UnitInfo.asDbModelTotalLiquid(detailPid: Long): TotalLiquid {
    return TotalLiquid(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}

fun UnitInfo.asDbModelRain(detailPid: Long): Rain {
    return Rain(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}

fun UnitInfo.asDbModelSnow(detailPid: Long): Snow {
    return Snow(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}

fun UnitInfo.asDbModelIce(detailPid: Long): Ice {
    return Ice(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}

fun UnitInfo.asDbModelEvapotranspiration(detailPid: Long): Evapotranspiration {
    return Evapotranspiration(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}

fun UnitInfo.asDbModelSolarIrradiance(detailPid: Long): SolarIrradiance {
    return SolarIrradiance(
        pid = -1L,
        value_ = value,
        unit = unit,
        unit_type = unitType,
        phrase = phrase,
        detail_pid = detailPid
    )
}
