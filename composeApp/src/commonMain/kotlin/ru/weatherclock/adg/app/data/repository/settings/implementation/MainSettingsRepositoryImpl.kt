package ru.weatherclock.adg.app.data.repository.settings.implementation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.data.repository.settings.MainSettingsRepository
import ru.weatherclock.adg.app.domain.model.settings.AppSettings
import ru.weatherclock.adg.app.domain.model.settings.ColorTheme
import ru.weatherclock.adg.app.domain.model.settings.orDefault

@OptIn(ExperimentalCoroutinesApi::class)
class MainSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>):
    MainSettingsRepository {

    override val config: Flow<AppSettings>
        get() = appSettings.updates.mapLatest { it.orDefault() }

    override suspend fun getConfig(): AppSettings {
        return appSettings.get().orDefault()
    }

    override suspend fun saveConfig(callback: AppSettings.() -> AppSettings) {
        appSettings.update {
            callback(it.orDefault())
        }
    }

    override suspend fun getColorTheme(): ColorTheme {
        return getConfig().colorTheme
    }

    override suspend fun setColorTheme(theme: ColorTheme) {
        saveConfig { copy(colorTheme = theme) }
    }
}