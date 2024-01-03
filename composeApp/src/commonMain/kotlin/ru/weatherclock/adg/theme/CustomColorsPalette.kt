package ru.weatherclock.adg.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColorsPalette(
    val background: Color = Color.Unspecified,
    val clockText: Color = Color.Unspecified,
    val dateDay: Color = Color.Unspecified,
    val dateMonth: Color = Color.Unspecified,
    val dateYear: Color = Color.Unspecified,
    val divider: Color = Color.Unspecified,
    val calendarWeekdayWorkText: Color = Color.Unspecified,
    val calendarWeekdayWeekendText: Color = Color.Unspecified,
    val calendarWeekdayBorder: Color = Color.Unspecified,
    val calendarDayDefaultText: Color = Color.Unspecified,
    val calendarDaySelectedText: Color = Color.Unspecified,
    val calendarDaySelectedBackground: Color = Color.Unspecified,
    val calendarDayWeekendBackground: Color = Color.Unspecified,
    val calendarDayHolidayBackground: Color = Color.Unspecified,
    val calendarDayHover: Color = Color.Unspecified,
    val calendarDayAdditionalDayOff: Color = Color(0xff00ffc6),
    val calendarDayNationalHoliday: Color = Color(0xffffeb08),
    val calendarDayPreHoliday: Color = Color(0xff03fd3d),
    val calendarDayRegionalHoliday: Color = Color(0xfff86f10),
)

val LightCustomColorsPalette = CustomColorsPalette(
    background = light_background,
    clockText = light_clockText,
    dateDay = light_dateDay,
    dateMonth = light_dateMonth,
    dateYear = light_dateYear,
    divider = light_divider,
    calendarWeekdayWorkText = light_calendarWeekdayWorkText,
    calendarWeekdayWeekendText = light_calendarWeekdayWeekendText,
    calendarWeekdayBorder = light_calendarWeekdayBorder,
    calendarDayDefaultText = light_calendarDayDefaultText,
    calendarDaySelectedText = light_calendarDaySelectedText,
    calendarDaySelectedBackground = light_calendarDaySelectedBackground,
    calendarDayWeekendBackground = light_calendarDayWeekendBackground,
    calendarDayHolidayBackground = light_calendarDayHolidayBackground,
    calendarDayHover = light_calendarDayHover,
    calendarDayAdditionalDayOff = light_calendarDayAdditionalDayOff,
    calendarDayNationalHoliday = light_calendarDayNationalHoliday,
    calendarDayPreHoliday = light_calendarDayPreHoliday,
    calendarDayRegionalHoliday = light_calendarDayRegionalHoliday,
)

val DarkCustomColorsPalette = CustomColorsPalette(
    background = dark_background,
    clockText = dark_clockText,
    dateDay = dark_dateDay,
    dateMonth = dark_dateMonth,
    dateYear = dark_dateYear,
    divider = dark_divider,
    calendarWeekdayWorkText = dark_calendarWeekdayWorkText,
    calendarWeekdayWeekendText = dark_calendarWeekdayWeekendText,
    calendarWeekdayBorder = dark_calendarWeekdayBorder,
    calendarDayDefaultText = dark_calendarDayDefaultText,
    calendarDaySelectedText = dark_calendarDaySelectedText,
    calendarDaySelectedBackground = dark_calendarDaySelectedBackground,
    calendarDayWeekendBackground = dark_calendarDayWeekendBackground,
    calendarDayHolidayBackground = dark_calendarDayHolidayBackground,
    calendarDayHover = dark_calendarDayHover,
    calendarDayAdditionalDayOff = dark_calendarDayAdditionalDayOff,
    calendarDayNationalHoliday = dark_calendarDayNationalHoliday,
    calendarDayPreHoliday = dark_calendarDayPreHoliday,
    calendarDayRegionalHoliday = dark_calendarDayRegionalHoliday,
)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }