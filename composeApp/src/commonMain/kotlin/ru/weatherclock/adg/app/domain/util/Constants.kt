package ru.weatherclock.adg.app.domain.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

val UNSPECIFIED_DATE: Long = LocalDateTime(
    1970,
    1,
    1,
    0,
    0
).toInstant(TimeZone.currentSystemDefault()).epochSeconds