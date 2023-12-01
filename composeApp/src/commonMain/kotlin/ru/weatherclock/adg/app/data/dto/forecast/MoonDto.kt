package ru.weatherclock.adg.app.data.dto.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.forecast.Moon
import ru.weatherclock.adg.app.domain.util.UNSPECIFIED_DATE

@Serializable
data class MoonDto(
    /**
     * Восход
     */
    @SerialName("EpochRise")
    val rise: Long = UNSPECIFIED_DATE,

    /**
     * Закат
     */
    @SerialName("EpochSet")
    val set: Long = UNSPECIFIED_DATE,

    /**
     * Фаза
     */
    @SerialName("Phase")
    val phase: String = "",

    /**
     * Число дней после новолуния.
     */
    @SerialName("Age")
    val age: Int = -1
)

fun MoonDto.asDomainModel(): Moon = Moon(
    rise = rise,
    set = set,
    phase = phase,
    age = age
)
