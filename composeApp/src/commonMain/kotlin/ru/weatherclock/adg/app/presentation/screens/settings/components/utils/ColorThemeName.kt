package ru.weatherclock.adg.app.presentation.screens.settings.components.utils

import androidx.compose.runtime.Composable
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.setting_theme_day
import homeweatherclock.composeapp.generated.resources.setting_theme_night
import homeweatherclock.composeapp.generated.resources.setting_theme_system
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.app.data.dto.ColorTheme

@Composable
fun ColorTheme.getName(): String = when (this) {
    ColorTheme.Day -> stringResource(Res.string.setting_theme_day)
    ColorTheme.Night -> stringResource(Res.string.setting_theme_night)
    ColorTheme.System -> stringResource(Res.string.setting_theme_system)
}