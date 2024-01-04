package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.compose.stringResource
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.domain.model.calendar.DayType
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.model.calendar.typeText
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.app.presentation.components.text.toMonthName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

typealias CalendarCallback = (selectedDate: LocalDateTime, prodCalendarDay: ProdCalendarDay?) -> Unit

data class CalendarCallbackData(
    val selectedDate: LocalDateTime = LocalDateTime.now(),
    val prodCalendarDay: ProdCalendarDay? = null
)

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

@Composable
fun LocalDateTime.toMessageDateString(): String {
    val day = date.dayOfMonth
        .toString()
        .padStart(
            2,
            '0'
        )
    val month = date.monthNumber.toMonthName()
    val year = date.year
    return "$day $month $year"
}

fun LocalDate.isWeekend(): Boolean {
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
}

@Composable
fun ProdCalendarDay?.stringForToast(
    selectedDate: LocalDate? = null,
    isSimple: Boolean = false
): String {
    val prodCalendarDay = this
    return buildString {
        val typeText = prodCalendarDay?.type?.typeText
        if (prodCalendarDay != null) {
            if (!typeText.isNullOrBlank() && !isSimple) {
                append(typeText)
                if (prodCalendarDay.note.isNotBlank()) {
                    append(": ")
                    append(prodCalendarDay.note)
                }
            } else {
                append(prodCalendarDay.note)
            }
        } else if (selectedDate != null) {
            if (selectedDate.isWeekend()) {
                append(stringResource(MR.strings.day_description_weekend))
            } else {
                append(stringResource(MR.strings.day_description_standard))
            }
        }
    }
}