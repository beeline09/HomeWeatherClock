package ru.weatherclock.adg.app.domain.model.calendar

import kotlinx.datetime.LocalDate
import ru.weatherclock.adg.db.ProdCalendar

data class ProdCalendarDay(
    val date: LocalDate,
    val type: DayType,
    val note: String,
    val weekDay: String = "",
    val workingHours: Int = 0
)

sealed class DayType {
    data object WorkingDay: DayType()
    data object Weekend: DayType()
    data class NationalHoliday(val typeText: String): DayType()
    data class RegionalHoliday(val typeText: String): DayType()
    data class PreHoliday(val typeText: String): DayType()
    data class AdditionalDayOff(val typeText: String): DayType()
}

fun Int.toDayType(typeText: String) = when (this) {
    2 -> DayType.Weekend
    3 -> DayType.NationalHoliday(typeText)
    4 -> DayType.RegionalHoliday(typeText)
    5 -> DayType.PreHoliday(typeText)
    6 -> DayType.AdditionalDayOff(typeText)
    else -> DayType.WorkingDay
}

fun DayType.toDbData(): Pair<Int, String> = when (this) {
    is DayType.AdditionalDayOff -> 6 to typeText
    is DayType.NationalHoliday -> 3 to typeText
    is DayType.PreHoliday -> 5 to typeText
    is DayType.RegionalHoliday -> 4 to typeText
    DayType.Weekend -> 2 to ""
    DayType.WorkingDay -> 1 to ""
}

fun ProdCalendarDay.asDbModel(): ProdCalendar {
    val typeData = type.toDbData()
    return ProdCalendar(
        date = date.toEpochDays().toLong(),
        type_id = typeData.first.toLong(),
        type_text = typeData.second,
        note = note,
        week_day = weekDay,
        working_hours = workingHours.toLong()
    )
}

fun ProdCalendar.asDomainModel(): ProdCalendarDay = ProdCalendarDay(
    date = LocalDate.fromEpochDays(date.toInt()),
    type = type_id.toInt().toDayType(type_text.orEmpty()),
    note = note.orEmpty()
)
