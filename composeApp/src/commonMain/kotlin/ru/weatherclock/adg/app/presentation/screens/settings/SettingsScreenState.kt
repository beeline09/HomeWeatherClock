package ru.weatherclock.adg.app.presentation.screens.settings

import ru.weatherclock.adg.app.domain.model.WeatherSettings
import ru.weatherclock.adg.app.presentation.components.viewModel.State

data class SettingsScreenState(
    val settings: WeatherSettings
): State
