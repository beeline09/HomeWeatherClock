package ru.weatherclock.adg.app.data.repository.settings

import kotlinx.coroutines.flow.Flow
import ru.weatherclock.adg.app.domain.model.settings.CalendarConfig

interface CalendarSettingsRepository {

    val config: Flow<CalendarConfig>
    suspend fun getConfig(): CalendarConfig
    suspend fun saveConfig(callback: CalendarConfig.() -> CalendarConfig)

    suspend fun isTextCalendarEnabled(): Boolean
    suspend fun setTextCalendarEnabled(enabled: Boolean)

    suspend fun isGridCalendarEnabled(): Boolean
    suspend fun setGridCalendarEnabled(enabled: Boolean)

}