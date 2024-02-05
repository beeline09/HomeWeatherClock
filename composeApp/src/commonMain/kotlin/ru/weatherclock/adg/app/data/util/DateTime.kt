package ru.weatherclock.adg.app.data.util

import kotlin.math.abs
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toLocalDateTime
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.app.presentation.components.util.padStart

fun Long.epochSecondsToLocalDateTime(): LocalDateTime {
    return Instant
        .fromEpochSeconds(epochSeconds = this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
}

fun Long.epochSecondsToLocalDate(): LocalDate {
    return epochSecondsToLocalDateTime().date
}

fun LocalDateTime.atStartOfDay(): LocalDateTime {
    return LocalDateTime.now().date.atTime(
        0,
        0,
        0,
        0
    )
}

fun LocalDateTime.atEndOfDay(): LocalDateTime {
    return LocalDateTime.now().date.atTime(
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
        hour.padStart(2)
    )
    if (withDots) {
        append(":")
    } else {
        append(" ")
    }
    append(
        minute.padStart(2)
    )
}

fun LocalDateTime.hourStr(withLeadingZero: Boolean = true) = buildString {
    append(hour.toString().let {
        if (withLeadingZero) {
            it.padStart(
                2,
                '0'
            )
        } else it
    })
}

fun LocalDateTime.minuteStr(withLeadingZero: Boolean = true) = buildString {
    append(minute.toString().let {
        if (withLeadingZero) {
            it.padStart(
                2,
                '0'
            )
        } else it
    })
}

fun LocalDateTime.secondStr(withLeadingZero: Boolean = true) = buildString {
    append(second.toString().let {
        if (withLeadingZero) {
            it.padStart(
                2,
                '0'
            )
        } else it
    })
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
    return isEqualsByHour(
        other,
        0
    )
}

fun LocalDateTime.isEqualsByHour(
    other: LocalDateTime,
    countsOfHour: Int
): Boolean {
    if (year != other.year) return false
    if (monthNumber != other.monthNumber) return false
    if (dayOfMonth != other.dayOfMonth) return false
    return abs(hour - other.hour) <= countsOfHour
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

fun LocalDate.isEqualsByDayOfMonth(other: LocalDate): Boolean {
    if (year != other.year) return false
    if (monthNumber != other.monthNumber) return false
    return dayOfMonth == other.dayOfMonth
}

fun LocalDate.isEqualsByMonthNumber(other: LocalDate): Boolean {
    if (year != other.year) return false
    return monthNumber == other.monthNumber
}

fun LocalDate.isEqualsByYear(other: LocalDate): Boolean {
    return year == other.year
}

fun LocalDate.toDbFormat(): Long {
    return "${year.padStart(4)}${monthNumber.padStart(2)}${dayOfMonth.padStart(2)}".toLong()
}

fun Long.fromDbToLocalDate(): LocalDate {
    val str = toString()
    if (str.length != 8) error("Parsed length must be 8!!!")
    val year = str.substring(
        0,
        4
    ).toInt()
    val month = str.substring(
        4,
        6
    ).toInt()
    val day = str.substring(
        6,
        8
    ).toInt()
    return LocalDate(
        year = year,
        monthNumber = month,
        dayOfMonth = day
    )
}

fun LocalDateTime.toDbFormat(): Long {
    val year = year.padStart(4)
    val month = monthNumber.padStart(2)
    val day = dayOfMonth.padStart(2)
    val hour = hour.padStart(2)
    val minute = minute.padStart(2)
    val second = second.padStart(2)
    return "$year$month$day$hour$minute$second".toLong()
}

fun Long.fromDbToLocalDateTime(): LocalDateTime {
    val str = toString()
    if (str.length != 14) error("Parsed length must be 14!!!")
    val year = str.substring(
        0,
        4
    ).toInt()
    val month = str.substring(
        4,
        6
    ).toInt()
    val day = str.substring(
        6,
        8
    ).toInt()
    val hour = str.substring(
        8,
        10
    ).toInt()
    val minute = str.substring(
        10,
        12
    ).toInt()
    val second = str.substring(
        12,
        14
    ).toInt()
    return LocalDateTime(
        year = year,
        monthNumber = month,
        dayOfMonth = day,
        hour = hour,
        minute = minute,
        second = second,
    )
}