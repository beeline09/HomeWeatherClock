package ru.weatherclock.adg.app.presentation.screens.settings.components

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import ru.weatherclock.adg.app.domain.model.settings.*
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.*

@Composable
fun LazyItemScope.SettingsListItem(item: BaseSettingItem) {
    when (item) {
        is SettingsHeader -> HeaderSettingsItem(item)
        is BooleanSetting -> BooleanSettingsItem(item)
        is HoursRangeSetting -> HoursSettingsItem(item)
        is StringListSetting -> ListStringSettingsItem(item)
        is StringSetting -> StringSettingsItem(item)
        is ColorsThemeSetting -> ColorsThemeSettingItem(item)
        is RussiaRegionSetting -> RussiaRegionSettingsItem(item)
        is WeatherApiLanguageSetting -> ApiLanguageSettingsItem(item)
        is WeatherApiListSetting -> ApiServerSettingsItem(item)
        is AutoCompleteSearchSetting -> AutocompleteSearchSettingsItem(item)
    }
}