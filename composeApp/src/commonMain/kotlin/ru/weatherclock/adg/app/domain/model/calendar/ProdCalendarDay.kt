package ru.weatherclock.adg.app.domain.model.calendar

import kotlinx.datetime.LocalDate

data class ProdCalendarDay(
    val date: LocalDate,
    val type: DayType,
    val note: String,
    val weekDay: String = "",
    val workingHours: Int = 0,
    val region: Int = 0,
)

sealed class DayType { data object WorkingDay: DayType()
    data object Weekend: DayType()
    data class NationalHoliday(val typeText: String): DayType()
    data class RegionalHoliday(val typeText: String): DayType()
    data class PreHoliday(val typeText: String): DayType()
    data class AdditionalDayOff(val typeText: String): DayType()
}

val DayType.typeText: String?
    get() = when (this) {
        is DayType.AdditionalDayOff -> typeText
        is DayType.NationalHoliday -> typeText
        is DayType.PreHoliday -> typeText
        is DayType.RegionalHoliday -> typeText
        else -> null
    }

