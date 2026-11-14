package ru.weatherclock.adg.app.domain.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Текущее локальное время. Живёт в domain (раньше — в presentation:
 * dateTypes, из-за чего use case'ы зависели от UI-слоя).
 */
@OptIn(kotlin.time.ExperimentalTime::class)
fun LocalDateTime.Companion.now(): LocalDateTime =
    kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

@OptIn(kotlin.time.ExperimentalTime::class)
fun LocalDate.Companion.now(): LocalDate =
    kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
