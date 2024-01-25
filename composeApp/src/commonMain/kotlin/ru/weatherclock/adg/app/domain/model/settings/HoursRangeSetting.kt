package ru.weatherclock.adg.app.domain.model.settings

data class HoursRangeSetting(
    override val settingsKey: SettingKey,
    override val currentValue: Pair<Int, Int>,
    override val onChange: (newValue: Pair<Int, Int>) -> Unit,
    override val isEnabled: Boolean = true,
): SettingsItem<Pair<Int, Int>>