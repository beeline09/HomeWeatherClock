package ru.weatherclock.adg.app.domain.model.calendar

import kotlinx.datetime.LocalDate
import ru.weatherclock.adg.app.data.util.fromDbToLocalDate
import ru.weatherclock.adg.app.data.util.toDbFormat
import ru.weatherclock.adg.db.ProdCalendar.ProdCalendar

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

fun ProdCalendarDay.asDbModel(region: Int): ProdCalendar {
    val typeData = type.toDbData()
    return ProdCalendar(
        date = date.toDbFormat(),
        type_id = typeData.first,
        type_text = typeData.second,
        note = note,
        week_day = weekDay,
        working_hours = workingHours,
        region = region
    )
}

fun ProdCalendar.asDomainModel(): ProdCalendarDay = ProdCalendarDay(
    date = date.fromDbToLocalDate(),
    type = type_id.toDayType(type_text.orEmpty()),
    note = note.orEmpty(),
    region = region,
    weekDay = week_day.orEmpty(),
    workingHours = working_hours ?: 0
)

val DayType.typeText: String?
    get() = when (this) {
        is DayType.AdditionalDayOff -> typeText
        is DayType.NationalHoliday -> typeText
        is DayType.PreHoliday -> typeText
        is DayType.RegionalHoliday -> typeText
        else -> null
    }

fun ProdCalendarDay?.stringForCalendar(): String? {
    return if (!this?.note.isNullOrBlank()) {
        this?.note
    } else this?.type?.typeText
}
