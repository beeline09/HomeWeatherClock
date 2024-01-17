package ru.weatherclock.adg.app.domain.model.forecast

import ru.weatherclock.adg.db.DegreeDaySummary

data class DaySummary(
    val heating: UnitInfo,
    val cooling: UnitInfo
)

fun DaySummary.asDbModel(forecastPid: Long): DegreeDaySummary {
    return DegreeDaySummary(
        cooling_phrase = cooling.phrase,
        cooling_unit = cooling.unit,
        cooling_unit_type = cooling.unitType,
        cooling_value = cooling.value,
        heating_value = heating.value,
        heating_phrase = heating.phrase,
        heating_unit = heating.unit,
        heating_unit_type = heating.unitType,
        forecast_pid = forecastPid,
        pid = -1L
    )
}

fun DegreeDaySummary.asDomainModel(): DaySummary {
    return DaySummary(
        heating = UnitInfo(
            value = heating_value,
            unit = heating_unit,
            unitType = heating_unit_type,
            phrase = heating_phrase
        ),
        cooling = UnitInfo(
            value = cooling_value,
            unit = cooling_unit,
            unitType = cooling_unit_type,
            phrase = cooling_phrase
        )
    )
}