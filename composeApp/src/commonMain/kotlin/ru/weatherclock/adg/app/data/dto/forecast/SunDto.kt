package ru.weatherclock.adg.app.data.dto.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.util.epochSecondsToLocalDateTime
import ru.weatherclock.adg.app.domain.model.forecast.Sun
import ru.weatherclock.adg.app.domain.util.UNSPECIFIED_DATE

@Serializable
data class SunDto(
    /**
     * Восход
     */
    @SerialName("EpochRise")
    val rise: Long = UNSPECIFIED_DATE,

    /**
     * Закат
     */
    @SerialName("EpochSet")
    val set: Long = UNSPECIFIED_DATE
)

fun SunDto.asDomainModel(): Sun = Sun(
    rise = rise.epochSecondsToLocalDateTime(),
    set = set.epochSecondsToLocalDateTime()
)
