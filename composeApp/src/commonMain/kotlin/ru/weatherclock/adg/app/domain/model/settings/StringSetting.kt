package ru.weatherclock.adg.app.domain.model.settings

data class StringSetting(
    override val settingsKey: SettingKey,
    override val currentValue: String,
    override val onChange: (newValue: String) -> Unit,
    override val isEnabled: Boolean = true,
): SettingsItem<String>