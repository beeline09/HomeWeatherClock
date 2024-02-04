package ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.db.Accuweather.DegreeDaySummary

@Serializable
data class DaySummaryDto(
    @SerialName("Heating")
    val heating: UnitDto = UnitDto(),
    @SerialName("Cooling")
    val cooling: UnitDto = UnitDto()
)

fun DaySummaryDto.asAccuweatherDbModel(forecastPid: Long): DegreeDaySummary {
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