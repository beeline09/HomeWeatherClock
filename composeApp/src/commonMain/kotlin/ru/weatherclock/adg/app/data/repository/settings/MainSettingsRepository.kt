package ru.weatherclock.adg.app.data.repository.settings

import kotlinx.coroutines.flow.Flow
import ru.weatherclock.adg.app.domain.model.settings.AppSettings
import ru.weatherclock.adg.app.domain.model.settings.ColorTheme

interface MainSettingsRepository {

    val config: Flow<AppSettings>
    suspend fun getConfig(): AppSettings
    suspend fun saveConfig(callback: AppSettings.() -> AppSettings)

    suspend fun getColorTheme(): ColorTheme
    suspend fun setColorTheme(theme: ColorTheme)

}