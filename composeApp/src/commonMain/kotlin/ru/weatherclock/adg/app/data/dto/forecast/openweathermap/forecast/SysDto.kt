package ru.weatherclock.adg.app.data.dto.forecast.openweathermap.forecast

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
