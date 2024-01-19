package ru.weatherclock.adg.app.data.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toLocalDateTime
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now

fun Long.epochSecondsToLocalDateTime(): LocalDateTime {
    return Instant
        .fromEpochSeconds(epochSeconds = this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
}

fun Long.epochSecondsToLocalDate(): LocalDate {
    return epochSecondsToLocalDateTime().date
}

fun LocalDateTime.atStartOfDay(): LocalDateTime {
    return LocalDateTime.now().date
        .atTime(
            0,
            0,
            0,
            0
        )
}

fun LocalDateTime.atEndOfDay(): LocalDateTime {
    return LocalDateTime.now().date
        .atTime(
            23,
            59,
            59,
            0
        )
}

fun LocalDateTime.atStartOfSecond(): LocalDateTime {
    return with(LocalDateTime.now().date) {
        atTime(
            hour,
            minute,
            second,
            0
        )
    }
}

fun LocalDateTime.atStartOfMinute(): LocalDateTime {
    return with(LocalDateTime.now().date) {
        atTime(
            hour,
            minute,
            0,
            0
        )
    }
}

fun LocalDateTime.atStartOfHour(): LocalDateTime {
    return with(LocalDateTime.now().date) {
        atTime(
            hour,
            0,
            0,
            0
        )
    }
}

fun LocalDateTime.timeStr(withDots: Boolean = true) = buildString {
    append(
        hour
            .toString()
            .padStart(
                2,
                '0'
            )
    )
    if (withDots) {
        append(":")
    } else {
        append(" ")
    }
    append(
        minute
            .toString()
            .padStart(
                2,
                '0'
            )
    )
}

fun LocalDateTime.isEqualsBySecond(other: LocalDateTime): Boolean {
    if (year != other.year) return false
    if (monthNumber != other.monthNumber) return false
    if (dayOfMonth != other.dayOfMonth) return false
    if (hour != other.hour) return false
    if (minute != other.minute) return false
    return second == other.second
}

fun LocalDateTime.isEqualsByMinute(other: LocalDateTime): Boolean {
    if (year != other.year) return false
    if (monthNumber != other.monthNumber) return false
    if (dayOfMonth != other.dayOfMonth) return false
    if (hour != other.hour) return false
    return minute == other.minute
}

fun LocalDateTime.isEqualsByHour(other: LocalDateTime): Boolean {
    if (year != other.year) return false
    if (monthNumber != other.monthNumber) return false
    if (dayOfMonth != other.dayOfMonth) return false
    return hour == other.hour
}

fun LocalDateTime.isEqualsByDayOfMonth(other: LocalDateTime): Boolean {
    if (year != other.year) return false
    if (monthNumber != other.monthNumber) return false
    return dayOfMonth == other.dayOfMonth
}

fun LocalDateTime.isEqualsByMonthNumber(other: LocalDateTime): Boolean {
    if (year != other.year) return false
    return monthNumber == other.monthNumber
}

fun LocalDateTime.isEqualsByYear(other: LocalDateTime): Boolean {
    return year == other.year
}