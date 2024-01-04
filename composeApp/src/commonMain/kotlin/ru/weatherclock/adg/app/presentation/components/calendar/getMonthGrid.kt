package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

internal fun LocalDateTime.getMonthGrid(): List<List<Int?>> {
    val year = year
    val month = month
    val firstDayOfMonth = LocalDateTime(
        year,
        month,
        1,
        0,
        0,
        0
    )
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.isoDayNumber % 7 - 1
    val daysInMonth = getLastDayOfMonth()
    val grid = mutableListOf<List<Int?>>()
    val week = mutableListOf<Int?>()
    var day = 1
    // Add nulls for days before the first day of the month
    for (i in 1..firstDayOfWeek) {
        week.add(null)
    }
    // Add days of the current month
    while (day <= daysInMonth) {
        week.add(day)
        if (week.size == 7) {
            grid.add(week.toList())
            week.clear()
        }
        day++
    }
    // Add nulls for days after the last day of the month
    while (week.size < 7) {
        week.add(null)
    }
    grid.add(week.toList())
    if (grid.last().all { it == null }) {
        grid.removeLast()
    }
    return grid.toList()
}

fun LocalDateTime.getLastDayOfMonth(): Int {
    return LocalDate(
        year,
        month,
        1
    ).plus(
        1,
        DateTimeUnit.MONTH
    )
        .minus(
            1,
            DateTimeUnit.DAY
        ).dayOfMonth
}