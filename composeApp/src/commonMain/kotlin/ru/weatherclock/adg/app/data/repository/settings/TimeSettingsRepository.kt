package ru.weatherclock.adg.app.data.repository.settings

import kotlinx.coroutines.flow.Flow
import ru.weatherclock.adg.app.domain.model.settings.TimeConfig

interface TimeSettingsRepository {

    val config: Flow<TimeConfig>
    suspend fun getConfig(): TimeConfig
    suspend fun saveConfig(callback: TimeConfig.() -> TimeConfig)

    suspend fun isDotsFlashEnabled(): Boolean
    suspend fun setDotsFlashEnabled(enabled: Boolean)

    suspend fun isDotsAnimated(): Boolean
    suspend fun setDotsAnimated(animated: Boolean)

    suspend fun isHourlyBeepEnabled(): Boolean
    suspend fun setHourlyBeepEnabled(enabled: Boolean)

    suspend fun getHourlyBeepStartHour(): Int
    suspend fun getHourlyBeepEndHour(): Int

    suspend fun setHourlyBeepStartHour(hour: Int)
    suspend fun setHourlyBeepEndHour(hour: Int)

    suspend fun isHourWithLeadingZero(): Boolean
    suspend fun setHourWithLeadingZero(withLeadingZero: Boolean)

}