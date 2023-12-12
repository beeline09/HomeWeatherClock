package ru.weatherclock.adg.app.data.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.serialization.RussianDateSerializer
import ru.weatherclock.adg.app.domain.model.calendar.DayType
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay

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
        type = when (typeId) {
            2 -> DayType.Weekend
            3 -> DayType.NationalHoliday(note)
            4 -> DayType.RegionalHoliday(note)
            5 -> DayType.PreHoliday(note)
            6 -> DayType.AdditionalDayOff(note)
            else -> DayType.WorkingDay
        },
        weekDay = weekDay,
        workingHours = workingHours
    )
}

fun List<CalendarDto>.asDomainModel(): List<ProdCalendarDay> = map { it.asDomainModel() }