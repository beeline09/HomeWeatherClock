package ru.weatherclock.adg.app.domain.model.calendar

import kotlinx.datetime.LocalDate

data class ProdCalendarDay(
    val date: LocalDate,
    val type: DayType,
    val weekDay: String = "",
    val workingHours: Int = 0
)

sealed class DayType {
    data object WorkingDay: DayType()
    data object Weekend: DayType()
    data class NationalHoliday(val note: String): DayType()
    data class RegionalHoliday(val note: String): DayType()
    data class PreHoliday(val note: String): DayType()
    data class AdditionalDayOff(val note: String): DayType()
}
