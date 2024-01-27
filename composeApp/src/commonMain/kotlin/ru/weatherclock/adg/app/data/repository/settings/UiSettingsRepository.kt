package ru.weatherclock.adg.app.data.repository.settings

import kotlinx.coroutines.flow.Flow
import ru.weatherclock.adg.app.domain.model.settings.AppSettings
import ru.weatherclock.adg.app.domain.model.settings.ColorTheme
import ru.weatherclock.adg.app.domain.model.settings.UiConfig

interface UiSettingsRepository {

    val allConfig: Flow<AppSettings>
    val uiConfig: Flow<UiConfig>
    suspend fun getConfig(): UiConfig
    suspend fun saveConfig(callback: UiConfig.() -> UiConfig)

    suspend fun getColorTheme(): ColorTheme
    suspend fun setColorTheme(theme: ColorTheme)

    suspend fun isElementsHideByTime(): Boolean
    suspend fun setElementsHideByTime(enabled: Boolean)

    suspend fun getElementsHideStartHour(): Int
    suspend fun setElementsHideStartHour(hour: Int)

    suspend fun getElementsHideEndHour(): Int
    suspend fun setElementsHideEndHour(hour: Int)

    suspend fun isWeatherHiddenByTime(): Boolean
    suspend fun isTextCalendarHiddenByTime(): Boolean
    suspend fun isGridCalendarHiddenByTime(): Boolean

    suspend fun setWeatherHiddenByTime(hidden: Boolean)
    suspend fun setTextCalendarHiddenByTime(hidden: Boolean)
    suspend fun setGridCalendarHiddenByTime(hidden: Boolean)

}