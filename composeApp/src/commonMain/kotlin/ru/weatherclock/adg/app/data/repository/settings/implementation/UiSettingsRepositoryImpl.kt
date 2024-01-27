package ru.weatherclock.adg.app.data.repository.settings.implementation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.data.repository.settings.UiSettingsRepository
import ru.weatherclock.adg.app.domain.model.settings.AppSettings
import ru.weatherclock.adg.app.domain.model.settings.ColorTheme
import ru.weatherclock.adg.app.domain.model.settings.UiConfig
import ru.weatherclock.adg.app.domain.model.settings.orDefault

@OptIn(ExperimentalCoroutinesApi::class)
class UiSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>): UiSettingsRepository {

    override val allConfig: Flow<AppSettings>
        get() = appSettings.updates.mapLatest { it.orDefault() }

    override val uiConfig: Flow<UiConfig>
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

    override suspend fun isElementsHideByTime(): Boolean {
        return getConfig().isHideElementsByTimeRange
    }

    override suspend fun setElementsHideByTime(enabled: Boolean) {
        saveConfig { copy(isHideElementsByTimeRange = enabled) }
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