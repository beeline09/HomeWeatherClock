package ru.weatherclock.adg.app.data.dto.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.forecast.Wind

@Serializable
data class WindDto(
    /**
     * Скорость ветра
     */
    @SerialName("Speed")
    val speed: UnitDto,

    /**
     * Направлени ветра
     */
    @SerialName("Direction")
    val direction: DirectionDto
)

fun WindDto.asDomainModel() = Wind(
    speed = speed.asDomainModel(),
    direction = direction.asDomainModel()
)