package ru.weatherclock.adg.app.presentation.screens.settings

import ru.weatherclock.adg.app.domain.model.settings.BaseSettingItem
import ru.weatherclock.adg.app.presentation.components.viewModel.State

data class SettingsScreenState(
    val settings: List<BaseSettingItem> = emptyList()
): State
