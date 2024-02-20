package ru.weatherclock.adg.app.presentation.components.text

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import androidx.compose.runtime.Composable
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.april_f
import homeweatherclock.composeapp.generated.resources.august_f
import homeweatherclock.composeapp.generated.resources.day_name_full_friday
import homeweatherclock.composeapp.generated.resources.day_name_full_monday
import homeweatherclock.composeapp.generated.resources.day_name_full_saturday
import homeweatherclock.composeapp.generated.resources.day_name_full_sunday
import homeweatherclock.composeapp.generated.resources.day_name_full_thursday
import homeweatherclock.composeapp.generated.resources.day_name_full_tuesday
import homeweatherclock.composeapp.generated.resources.day_name_full_wednesday
import homeweatherclock.composeapp.generated.resources.december_f
import homeweatherclock.composeapp.generated.resources.february_f
import homeweatherclock.composeapp.generated.resources.january_f
import homeweatherclock.composeapp.generated.resources.jule_f
import homeweatherclock.composeapp.generated.resources.june_f
import homeweatherclock.composeapp.generated.resources.march_f
import homeweatherclock.composeapp.generated.resources.may_f
import homeweatherclock.composeapp.generated.resources.november_f
import homeweatherclock.composeapp.generated.resources.october_f
import homeweatherclock.composeapp.generated.resources.september_f
import org.jetbrains.compose.resources.stringResource

@Composable
fun Int.toMonthName(): String = when (this) {
    1 -> stringResource(Res.string.january_f)
    2 -> stringResource(Res.string.february_f)
    3 -> stringResource(Res.string.march_f)
    4 -> stringResource(Res.string.april_f)
    5 -> stringResource(Res.string.may_f)
    6 -> stringResource(Res.string.june_f)
    7 -> stringResource(Res.string.jule_f)
    8 -> stringResource(Res.string.august_f)
    9 -> stringResource(Res.string.september_f)
    10 -> stringResource(Res.string.october_f)
    11 -> stringResource(Res.string.november_f)
    12 -> stringResource(Res.string.december_f)
    else -> ""
}

@Composable
fun LocalDate.formatForWeatherCell(): String = buildString {
    val dayName = dayOfWeek.getDayNameFull().lowercase().replaceFirstChar { it.uppercaseChar() }
    val monthName = monthNumber.toMonthName().lowercase()
    append(dayName)
    append(", ")
    append(dayOfMonth)
    append(" ")
    append(monthName)
}

@Composable
fun DayOfWeek.getDayNameFull(): String = when (this) {
    DayOfWeek.MONDAY -> stringResource(Res.string.day_name_full_monday)
    DayOfWeek.TUESDAY -> stringResource(Res.string.day_name_full_tuesday)
    DayOfWeek.WEDNESDAY -> stringResource(Res.string.day_name_full_wednesday)
    DayOfWeek.THURSDAY -> stringResource(Res.string.day_name_full_thursday)
    DayOfWeek.FRIDAY -> stringResource(Res.string.day_name_full_friday)
    DayOfWeek.SATURDAY -> stringResource(Res.string.day_name_full_saturday)
    DayOfWeek.SUNDAY -> stringResource(Res.string.day_name_full_sunday)
    else -> ""
}