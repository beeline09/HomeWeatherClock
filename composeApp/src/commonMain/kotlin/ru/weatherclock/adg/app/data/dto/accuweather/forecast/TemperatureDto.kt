package ru.weatherclock.adg.app.data.dto.accuweather.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.forecast.Temperature

@Serializable
data class TemperatureDto(

    /**
     * Минимальная температура
     */
    @SerialName("Minimum")
    val minimum: UnitDto = UnitDto(),

    /**
     * Максимальная температура
     */
    @SerialName("Maximum")
    val maximum: UnitDto = UnitDto()
)

fun TemperatureDto.asDomainModel(): Temperature = Temperature(
    minimum = minimum.asDomainModel(),
    maximum = maximum.asDomainModel()
)