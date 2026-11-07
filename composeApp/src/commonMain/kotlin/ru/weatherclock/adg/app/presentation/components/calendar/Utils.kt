@file:OptIn(ExperimentalResourceApi::class)

package ru.weatherclock.adg.app.presentation.components.calendar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.day_description_day_off
import homeweatherclock.composeapp.generated.resources.day_description_holiday
import homeweatherclock.composeapp.generated.resources.day_description_preholiday
import homeweatherclock.composeapp.generated.resources.day_description_standard
import homeweatherclock.composeapp.generated.resources.day_description_weekend
import homeweatherclock.composeapp.generated.resources.prod_calendar_preholiday_before
import homeweatherclock.composeapp.generated.resources.prod_calendar_transferred_from
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
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
    val day = date.day
        .toString()
        .padStart(
            2,
            '0'
        )
    val month = date.month.number.toMonthName()
    val year = date.year
    return "$day $month $year"
}

fun LocalDate.isWeekend(): Boolean {
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY
}

@Composable
fun ProdCalendarDay?.stringForToast(
    selectedDate: LocalDate? = null,
    @Suppress("UNUSED_PARAMETER") isSimple: Boolean = false
): String {
    val prodCalendarDay = this
    return if (prodCalendarDay != null) {
        localizedDescription().orEmpty()
    } else if (selectedDate != null) {
        if (selectedDate.isWeekend()) {
            stringResource(Res.string.day_description_weekend)
        } else {
            stringResource(Res.string.day_description_standard)
        }
    } else {
        ""
    }
}

/**
 * Описание текущего дня производственного календаря для подписи на главном
 * экране и снекбара. Фразы собираются из ресурсных шаблонов: note в данных
 * хранит только сырые значения (дата переноса «ММ.ДД», название праздника).
 */
@Composable
fun ProdCalendarDay?.localizedDescription(): String? {
    val day = this ?: return null
    return when (val type = day.type) {
        is DayType.NationalHoliday ->
            type.typeText.ifBlank { stringResource(Res.string.day_description_holiday) }

        is DayType.RegionalHoliday ->
            type.typeText.ifBlank { stringResource(Res.string.day_description_holiday) }

        is DayType.PreHoliday ->
            if (day.note.isNotBlank()) {
                stringResource(Res.string.prod_calendar_preholiday_before, day.note)
            } else {
                stringResource(Res.string.day_description_preholiday)
            }

        is DayType.AdditionalDayOff ->
            if (day.note.isNotBlank()) {
                stringResource(Res.string.prod_calendar_transferred_from, day.note)
            } else {
                stringResource(Res.string.day_description_day_off)
            }

        DayType.Weekend, DayType.WorkingDay -> null
    }
}