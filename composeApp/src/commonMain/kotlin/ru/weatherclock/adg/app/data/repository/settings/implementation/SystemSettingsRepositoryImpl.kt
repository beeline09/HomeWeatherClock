package ru.weatherclock.adg.app.data.repository.settings.implementation

import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.SystemConfig
import ru.weatherclock.adg.app.data.repository.settings.SystemSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.getSettings
import ru.weatherclock.adg.app.data.repository.settings.updateSettings

class SystemSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>) :
    SystemSettingsRepository {

    override suspend fun getConfig(): SystemConfig {
        return appSettings.getSettings().systemConfig
    }

    override suspend fun saveConfig(callback: SystemConfig.() -> SystemConfig) {
        appSettings.updateSettings { s ->
            s.copy(systemConfig = callback(s.systemConfig))
        }
    }
}