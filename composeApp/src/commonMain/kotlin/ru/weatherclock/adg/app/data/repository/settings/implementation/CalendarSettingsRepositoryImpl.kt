package ru.weatherclock.adg.app.data.repository.settings.implementation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.CalendarConfig
import ru.weatherclock.adg.app.data.dto.orDefault
import ru.weatherclock.adg.app.data.repository.settings.CalendarSettingsRepository

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>):
    CalendarSettingsRepository {

    override val config: Flow<CalendarConfig>
        get() = appSettings.updates.mapLatest { it.orDefault().calendarConfig }

    override suspend fun getConfig(): CalendarConfig {
        return appSettings.get().orDefault().calendarConfig
    }

    override suspend fun saveConfig(callback: CalendarConfig.() -> CalendarConfig) {
        appSettings.update {
            val s = it.orDefault()
            s.copy(calendarConfig = callback(s.calendarConfig))
        }
    }

    override suspend fun isTextCalendarEnabled(): Boolean {
        return getConfig().textCalendarEnabled
    }

    override suspend fun setTextCalendarEnabled(enabled: Boolean) {
        saveConfig { copy(textCalendarEnabled = enabled) }
    }

    override suspend fun isGridCalendarEnabled(): Boolean {
        return getConfig().gridCalendarEnabled
    }

    override suspend fun setGridCalendarEnabled(enabled: Boolean) {
        saveConfig { copy(gridCalendarEnabled = enabled) }
    }
}