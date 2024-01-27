package ru.weatherclock.adg.app.domain.model.settings

data class WeatherApiLanguageSetting(
    override val settingsKey: SettingKey,
    override val currentValue: WeatherApiLanguage,
    override val onChange: (newValue: WeatherApiLanguage) -> Unit,
    override val isEnabled: Boolean = true,
): SettingsItem<WeatherApiLanguage>