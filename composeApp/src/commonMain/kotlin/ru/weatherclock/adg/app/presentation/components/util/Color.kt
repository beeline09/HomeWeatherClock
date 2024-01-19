package ru.weatherclock.adg.app.presentation.components.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ru.weatherclock.adg.app.domain.model.forecast.Severity
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
fun Severity.getColor(): Color = with(LocalCustomColorsPalette.current) {
    when (this@getColor) {
        Severity.SIGNIFICANT -> weatherSeveritySignificant
        Severity.MAJOR -> weatherSeverityMajor
        Severity.MODERATE -> weatherSeverityModerate
        else -> weatherSeverityOther
    }
}