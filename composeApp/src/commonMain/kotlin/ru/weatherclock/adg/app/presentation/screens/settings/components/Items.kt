package ru.weatherclock.adg.app.presentation.screens.settings.components

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import ru.weatherclock.adg.app.domain.model.settings.BaseSettingItem
import ru.weatherclock.adg.app.domain.model.settings.BooleanSetting
import ru.weatherclock.adg.app.domain.model.settings.ColorsThemeSetting
import ru.weatherclock.adg.app.domain.model.settings.HoursRangeSetting
import ru.weatherclock.adg.app.domain.model.settings.IntSetting
import ru.weatherclock.adg.app.domain.model.settings.SettingsHeader
import ru.weatherclock.adg.app.domain.model.settings.StringListSetting
import ru.weatherclock.adg.app.domain.model.settings.StringSetting
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.BooleanSettingsItem
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.ColorsThemeSettingItem
import ru.weatherclock.adg.app.presentation.screens.settings.components.items.HeaderSettingsItem

@Composable
fun LazyItemScope.getListItem(
    index: Int,
    item: BaseSettingItem
) {
    when (item) {
        is SettingsHeader -> HeaderSettingsItem(item)
        is BooleanSetting -> BooleanSettingsItem(item)
        is HoursRangeSetting -> {}
        is IntSetting -> {}
        is StringListSetting -> {}
        is StringSetting -> {}
        is ColorsThemeSetting -> ColorsThemeSettingItem(item)
    }
}