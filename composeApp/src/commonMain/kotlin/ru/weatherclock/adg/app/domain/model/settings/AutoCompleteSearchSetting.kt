package ru.weatherclock.adg.app.domain.model.settings

import kotlinx.coroutines.flow.Flow
import ru.weatherclock.adg.app.data.dto.CityConfig
import ru.weatherclock.adg.app.data.dto.WeatherServer

data class AutoCompleteSearchSetting(
    override val settingsKey: SettingKey,
    override val currentValue: CityConfig,
    override val onChange: (newValue: CityConfig) -> Unit,
    override val isEnabled: Boolean = true,
    val onSearchTextChanged: (String) -> Unit,
    val searchResults: Flow<List<CityConfig>>,
    val currentServer: WeatherServer
) : SettingsItem<CityConfig>