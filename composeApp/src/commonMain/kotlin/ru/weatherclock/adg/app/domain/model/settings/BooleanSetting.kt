package ru.weatherclock.adg.app.domain.model.settings

data class BooleanSetting(
    override val settingsKey: SettingKey,
    override val currentValue: Boolean,
    override val onChange: (newValue: Boolean) -> Unit,
    override val isEnabled: Boolean = true,
): SettingsItem<Boolean>