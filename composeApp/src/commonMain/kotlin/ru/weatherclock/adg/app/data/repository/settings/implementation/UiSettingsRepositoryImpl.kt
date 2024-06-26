package ru.weatherclock.adg.app.data.repository.settings.implementation

import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.ColorTheme
import ru.weatherclock.adg.app.data.dto.UiConfig
import ru.weatherclock.adg.app.data.dto.orDefault
import ru.weatherclock.adg.app.data.repository.settings.UiSettingsRepository

class UiSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>): UiSettingsRepository {

    override val allConfigFlow: Flow<AppSettings>
        get() = appSettings.updates.mapLatest { it.orDefault() }

    override suspend fun getAllConfig(): AppSettings {
        return appSettings.get().orDefault()
    }

    override val uiConfigFlow: Flow<UiConfig>
        get() = appSettings.updates.mapLatest { it.orDefault().uiConfig }

    override suspend fun getConfig(): UiConfig {
        return appSettings.get().orDefault().uiConfig
    }

    override suspend fun saveConfig(callback: UiConfig.() -> UiConfig) {
        appSettings.update {
            val s = it.orDefault()
            s.copy(uiConfig = callback(s.uiConfig))
        }
    }

    override suspend fun getColorTheme(): ColorTheme {
        return getConfig().colorTheme
    }

    override suspend fun setColorTheme(theme: ColorTheme) {
        saveConfig { copy(colorTheme = theme) }
    }

    override suspend fun getElementsHideStartHour(): Int {
        return getConfig().hideStartHour
    }

    override suspend fun setElementsHideStartHour(hour: Int) {
        saveConfig { copy(hideStartHour = hour) }
    }

    override suspend fun getElementsHideEndHour(): Int {
        return getConfig().hideEndHour
    }

    override suspend fun setElementsHideEndHour(hour: Int) {
        saveConfig { copy(hideEndHour = hour) }
    }

    override suspend fun isWeatherHiddenByTime(): Boolean {
        return getConfig().isWeatherHidden
    }

    override suspend fun isTextCalendarHiddenByTime(): Boolean {
        return getConfig().isTextCalendarHidden
    }

    override suspend fun isGridCalendarHiddenByTime(): Boolean {
        return getConfig().isGridCalendarHidden
    }

    override suspend fun setWeatherHiddenByTime(hidden: Boolean) {
        saveConfig { copy(isWeatherHidden = hidden) }
    }

    override suspend fun setTextCalendarHiddenByTime(hidden: Boolean) {
        saveConfig { copy(isTextCalendarHidden = hidden) }
    }

    override suspend fun setGridCalendarHiddenByTime(hidden: Boolean) {
        saveConfig { copy(isGridCalendarHidden = hidden) }
    }
}