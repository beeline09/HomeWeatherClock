package ru.weatherclock.adg.app.domain.model.settings

import ru.weatherclock.adg.app.data.dto.WeatherIconSet
import ru.weatherclock.adg.app.data.dto.WeatherServer

data class WeatherIconSetSetting(
    override val settingsKey: SettingKey,
    override val currentValue: WeatherIconSet,
    override val onChange: (WeatherIconSet) -> Unit,
    override val isEnabled: Boolean = true,
    val currentServer: WeatherServer,
) : SettingsItem<WeatherIconSet>
