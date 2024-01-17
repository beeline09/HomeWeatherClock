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