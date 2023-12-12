package ru.weatherclock.adg.app.data.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.serialization.RussianDateSerializer
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.model.calendar.toDayType

@Serializable
data class CalendarDto(
    @SerialName("date")
    @Serializable(with = RussianDateSerializer::class)
    val date: LocalDate,
    @SerialName("type_id")
    val typeId: Int = 1,
    @SerialName("type_text")
    val typeText: String = "",
    @SerialName("note")
    val note: String = "",
    @SerialName("week_day")
    val weekDay: String = "",
    @SerialName("working_hours")
    val workingHours: Int = 0
)

fun CalendarDto.asDomainModel(): ProdCalendarDay {
    return ProdCalendarDay(
        date = date,
        type = typeId.toDayType(typeText),
        weekDay = weekDay,
        workingHours = workingHours,
        note = note
    )
}

fun List<CalendarDto>.asDomainModel(): List<ProdCalendarDay> = map { it.asDomainModel() }