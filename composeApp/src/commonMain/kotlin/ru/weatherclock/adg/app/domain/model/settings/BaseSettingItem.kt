package ru.weatherclock.adg.app.domain.model.settings

sealed interface BaseSettingItem {

    val settingsKey: SettingKey
}