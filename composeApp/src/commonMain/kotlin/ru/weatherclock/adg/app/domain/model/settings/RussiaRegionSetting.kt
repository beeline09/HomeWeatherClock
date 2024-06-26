package ru.weatherclock.adg.app.domain.model.settings

data class RussiaRegionSetting(
    override val settingsKey: SettingKey,
    override val currentValue: Int,
    override val onChange: (newValue: Int) -> Unit,
    override val isEnabled: Boolean = true,
): SettingsItem<Int>