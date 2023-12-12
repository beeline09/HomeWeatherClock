package ru.weatherclock.adg.app.data.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.serialization.RussianDateSerializer

@Serializable()
data class ProductionCalendarDto(
    @SerialName("country_code")
    val countryCode: String = "",
    @SerialName("country_text")
    val countryText: String = "",
    @SerialName("dt_start")
    @Serializable(with = RussianDateSerializer::class)
    val dateStart: LocalDate,
    @SerialName("dt_end")
    @Serializable(with = RussianDateSerializer::class)
    val dateEnd: LocalDate,
    @SerialName("work_week_type")
    val workWeekType: String = "",
    @SerialName("period")
    val period: String = "",
    @SerialName("note")
    val note: String = "",
    @SerialName("days")
    val days: List<CalendarDto> = emptyList()
)