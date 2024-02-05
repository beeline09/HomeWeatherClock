package ru.weatherclock.adg.app.data.dto.forecast.openweathermap.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.serialization.PartOfDaySerializer

@Serializable
data class SysDto(
    @SerialName("pod")
    val partOfDay: PartOfDay? = PartOfDay.Day
)

@Serializable(with = PartOfDaySerializer::class)
enum class PartOfDay {

    Day,
    Night
}

fun Int.toPartOfDay(): PartOfDay = when (this) {
    0 -> PartOfDay.Day
    else -> PartOfDay.Night
}

fun PartOfDay?.toInt(): Int = when (this) {
    PartOfDay.Day -> 0
    else -> 1
}
