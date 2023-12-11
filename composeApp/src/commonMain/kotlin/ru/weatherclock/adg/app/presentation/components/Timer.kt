package ru.weatherclock.adg.app.presentation.components

import kotlin.time.Duration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}