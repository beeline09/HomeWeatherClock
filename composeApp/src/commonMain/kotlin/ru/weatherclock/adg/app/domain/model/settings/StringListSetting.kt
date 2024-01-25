package ru.weatherclock.adg.app.domain.model.settings

data class StringListSetting(
    override val settingsKey: SettingKey,
    override val currentValue: List<String>,
    override val onChange: (newValue: List<String>) -> Unit,
    override val isEnabled: Boolean = true,
): SettingsItem<List<String>>