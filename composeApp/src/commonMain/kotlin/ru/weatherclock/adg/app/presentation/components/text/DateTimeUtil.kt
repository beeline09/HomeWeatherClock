package ru.weatherclock.adg.app.presentation.components.text

import ru.weatherclock.adg.Res

fun Int.toMonthName(): String = when (this) {
    1 -> Res.string.january_f
    2 -> Res.string.february_f
    3 -> Res.string.march_f
    4 -> Res.string.april_f
    5 -> Res.string.may_f
    6 -> Res.string.june_f
    7 -> Res.string.jule_f
    8 -> Res.string.august_f
    9 -> Res.string.september_f
    10 -> Res.string.october_f
    11 -> Res.string.november_f
    12 -> Res.string.december_f
    else -> ""
}