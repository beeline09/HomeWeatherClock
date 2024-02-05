package ru.weatherclock.adg.app.domain.model.settings

import ru.weatherclock.adg.app.data.dto.WeatherServer

data class WeatherApiListSetting(
    override val settingsKey: SettingKey,
    override val currentValue: WeatherServer,
    override val onChange: (newValue: WeatherServer) -> Unit,
    override val isEnabled: Boolean = true,
): SettingsItem<WeatherServer>

