package ru.weatherclock.adg.app.data.dto.accuweather.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.forecast.DaySummary

@Serializable
data class DaySummaryDto(
    @SerialName("Heating")
    val heating: UnitDto = UnitDto(),
    @SerialName("Cooling")
    val cooling: UnitDto = UnitDto()
)

fun DaySummaryDto.asDomainModel(): DaySummary = DaySummary(
    heating = heating.asDomainModel(),
    cooling = cooling.asDomainModel()
)