package ru.weatherclock.adg.app.data.dto.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.forecast.Moon

@Serializable
data class MoonDto(
    /**
     * Восход
     */
    @SerialName("EpochRise")
    val rise: Long,

    /**
     * Закат
     */
    @SerialName("EpochSet")
    val set: Long,

    /**
     * Фаза
     */
    @SerialName("Phase")
    val phase: String,

    /**
     * Число дней после новолуния.
     */
    @SerialName("Age")
    val age: Int
)

fun MoonDto.asDomainModel(): Moon = Moon(
    rise = rise,
    set = set,
    phase = phase,
    age = age
)
