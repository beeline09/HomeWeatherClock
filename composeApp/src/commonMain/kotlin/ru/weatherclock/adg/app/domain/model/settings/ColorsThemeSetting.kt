package ru.weatherclock.adg.app.domain.model.settings

import ru.weatherclock.adg.app.data.dto.ColorTheme

data class ColorsThemeSetting(
    override val settingsKey: SettingKey,
    override val currentValue: ColorTheme,
    override val onChange: (newValue: ColorTheme) -> Unit,
    override val isEnabled: Boolean = true,
): SettingsItem<ColorTheme>

