package ru.weatherclock.adg.app.data.repository.settings.implementation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.data.repository.settings.TimeSettingsRepository
import ru.weatherclock.adg.app.domain.model.settings.AppSettings
import ru.weatherclock.adg.app.domain.model.settings.TimeConfig
import ru.weatherclock.adg.app.domain.model.settings.orDefault

@OptIn(ExperimentalCoroutinesApi::class)
class TimeSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>):
    TimeSettingsRepository {

    override val config: Flow<TimeConfig>
        get() = appSettings.updates.mapLatest { it.orDefault().timeConfig }

    override suspend fun getConfig(): TimeConfig {
        return appSettings.get().orDefault().timeConfig
    }

    override suspend fun saveConfig(callback: TimeConfig.() -> TimeConfig) {
        appSettings.update {
            val s = it.orDefault()
            s.copy(timeConfig = callback(s.timeConfig))
        }
    }

    override suspend fun isDotsFlashEnabled(): Boolean {
        return getConfig().dotsFlashEnabled
    }

    override suspend fun setDotsFlashEnabled(enabled: Boolean) {
        saveConfig { copy(dotsFlashEnabled = enabled) }
    }

    override suspend fun isDotsAnimated(): Boolean {
        return getConfig().dotsFlashAnimated
    }

    override suspend fun setDotsAnimated(animated: Boolean) {
        saveConfig { copy(dotsFlashAnimated = animated) }
    }

    override suspend fun isHourlyBeepEnabled(): Boolean {
        return getConfig().hourlyBeepEnabled
    }

    override suspend fun setHourlyBeepEnabled(enabled: Boolean) {
        saveConfig { copy(hourlyBeepEnabled = enabled) }
    }

    override suspend fun getHourlyBeepStartHour(): Int {
        return getConfig().hourlyBeepStartHour
    }

    override suspend fun getHourlyBeepEndHour(): Int {
        return getConfig().hourlyBeepEndHour
    }

    override suspend fun setHourlyBeepStartHour(hour: Int) {
        saveConfig { copy(hourlyBeepStartHour = hour) }
    }

    override suspend fun setHourlyBeepEndHour(hour: Int) {
        saveConfig { copy(hourlyBeepEndHour = hour) }
    }
}