package ru.weatherclock.adg.app.presentation.components.calendar.stringAnnotation

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.number
import ru.weatherclock.adg.app.presentation.components.calendar.styles.DateInputDefaults

internal fun LocalDateTime.displayDate() =
    "${
        this.dayOfMonth.trailZeros()
    }${
        this.month.number.trailZeros()
    }${
        this.year
    }"

internal fun LocalDateTime.displayDateTime() =
    "${
        this.dayOfMonth.trailZeros()
    }${
        this.month.number.trailZeros()
    }${
        this.year
    }${
        this.hour.trailZeros()
    }${
        this.minute.trailZeros()
    }"

internal fun Int?.trailZeros() = this
    ?.toString()
    ?.padStart(
        2,
        '0'
    )

internal fun Month.toLocale(locale: DateInputDefaults.DateInputLocale) = when (locale) {
    DateInputDefaults.DateInputLocale.RU -> when (this) {
        Month.JANUARY -> "января"
        Month.FEBRUARY -> "февраля"
        Month.MARCH -> "марта"
        Month.APRIL -> "апреля"
        Month.MAY -> "мая"
        Month.JUNE -> "июня"
        Month.JULY -> "июля"
        Month.AUGUST -> "августа"
        Month.SEPTEMBER -> "сентября"
        Month.OCTOBER -> "октября"
        Month.NOVEMBER -> "ноября"
        Month.DECEMBER -> "декабря"
        else -> ""
    }.replaceFirstChar { it.titlecase() }

    DateInputDefaults.DateInputLocale.EN -> this.name
        .lowercase()
        .replaceFirstChar { it.titlecase() }
}

fun getApplyText(locale: DateInputDefaults.DateInputLocale): String {
    return when (locale) {
        DateInputDefaults.DateInputLocale.RU -> "Применить"
        DateInputDefaults.DateInputLocale.EN -> "Apply"
    }
}

internal fun String.isValidValue(range: IntRange): Boolean {
    if (isBlank()) {
        return true
    }
    val hourAsInt = trim().toIntOrNull() ?: return false
    return hourAsInt in range
}

internal fun String.isValidHour() = this.isValidValue(0..23)

internal fun String.isValidMinute() = this.isValidValue(0..59)

internal fun String.toTimeInt(): Int {
    return if (isBlank()) {
        0
    } else {
        trim().toInt()
    }
}