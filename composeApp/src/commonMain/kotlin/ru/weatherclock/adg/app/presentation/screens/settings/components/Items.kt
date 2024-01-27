package ru.weatherclock.adg.app.presentation.screens.settings.components

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import ru.weatherclock.adg.app.domain.model.settings.BaseSettingItem
import ru.weatherclock.adg.app.domain.model.settings.BooleanSetting
import ru.weatherclock.adg.app.domain.model.settings.ColorsThemeSetting
import ru.weatherclock.adg.app.domain.model.settings.HoursRangeSetting
import ru.weatherclock.adg.app.domain.model.settings.IntSetting
import ru.weatherclock.adg.app.domain.model.settings.RussiaRegionSetting
import ru.weatherclock.adg.app.domain.model.settings.SettingsHeader
import ru.weatherclock.adg.app.domain.model.settings.StringListSetting
import ru.weatherclock.adg.app.domain.model.settings.StringSetting
import ru.weatherclock.adg.app.domain.model.settings.WeatherApiLanguageSetting
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.ApiLanguageSettingsItem
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.BooleanSettingsItem
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.ColorsThemeSettingItem
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.HeaderSettingsItem
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.HoursSettingsItem
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.ListStringSettingsItem
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.RussiaRegionSettingsItem
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.StringSettingsItem

@Composable
fun LazyItemScope.getListItem(
    index: Int,
    item: BaseSettingItem
) {
    when (item) {
        is SettingsHeader -> HeaderSettingsItem(item)
        is BooleanSetting -> BooleanSettingsItem(item)
        is HoursRangeSetting -> HoursSettingsItem(item)
        is IntSetting -> {}
        is StringListSetting -> ListStringSettingsItem(item)
        is StringSetting -> StringSettingsItem(item)
        is ColorsThemeSetting -> ColorsThemeSettingItem(item)
        is RussiaRegionSetting -> RussiaRegionSettingsItem(item)
        is WeatherApiLanguageSetting -> ApiLanguageSettingsItem(item)
    }
}