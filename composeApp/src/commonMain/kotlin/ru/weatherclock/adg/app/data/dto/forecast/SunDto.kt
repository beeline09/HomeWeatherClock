package ru.weatherclock.adg.app.data.dto.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.forecast.Sun

@Serializable
data class SunDto(
    /**
     * Восход
     */
    @SerialName("EpochRise")
    val rise: Long,

    /**
     * Закат
     */
    @SerialName("EpochSet")
    val set: Long
)

fun SunDto.asDomainModel(): Sun = Sun(
    rise = rise,
    set = set
)
