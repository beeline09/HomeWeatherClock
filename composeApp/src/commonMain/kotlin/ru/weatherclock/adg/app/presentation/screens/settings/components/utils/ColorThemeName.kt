package ru.weatherclock.adg.app.presentation.screens.settings.components.utils

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.domain.model.settings.ColorTheme

@Composable
fun ColorTheme.getName(): String = when (this) {
    ColorTheme.Day -> stringResource(MR.strings.setting_theme_day)
    ColorTheme.Night -> stringResource(MR.strings.setting_theme_night)
    ColorTheme.System -> stringResource(MR.strings.setting_theme_system)
}