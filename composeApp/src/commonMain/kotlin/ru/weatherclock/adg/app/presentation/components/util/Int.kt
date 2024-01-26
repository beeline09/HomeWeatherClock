package ru.weatherclock.adg.app.presentation.components.util

fun Int.padStart(
    count: Int,
    padSymbol: Char = '0'
): String = toString().padStart(
    count,
    padSymbol
)