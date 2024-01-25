package ru.weatherclock.adg.app.domain.model.settings

sealed interface SettingsItem<T>: BaseSettingItem {

    val currentValue: T
    val isEnabled: Boolean
    val onChange: (newValue: T) -> Unit

}