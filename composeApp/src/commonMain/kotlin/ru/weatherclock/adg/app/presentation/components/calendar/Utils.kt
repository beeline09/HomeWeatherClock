package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ru.weatherclock.adg.app.domain.model.calendar.DayType
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

fun Int?.isCurrentDay(dateTime: LocalDateTime): Boolean {
    if (this == null) return false
    val date = dateTime.date
    val currentDate = LocalDate(
        date.year,
        date.month,
        this
    )
    return currentDate == date
}

@Composable
fun ProdCalendarDay.color(): Color {
    val palette = LocalCustomColorsPalette.current
    return when (this.type) {
        is DayType.AdditionalDayOff -> palette.calendarDayAdditionalDayOff
        is DayType.NationalHoliday -> palette.calendarDayNationalHoliday
        is DayType.PreHoliday -> palette.calendarDayPreHoliday
        is DayType.RegionalHoliday -> palette.calendarDayRegionalHoliday
        DayType.Weekend -> palette.calendarWeekdayWeekendText
        DayType.WorkingDay -> palette.calendarDayDefaultText
    }
}