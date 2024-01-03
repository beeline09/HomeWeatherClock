package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

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