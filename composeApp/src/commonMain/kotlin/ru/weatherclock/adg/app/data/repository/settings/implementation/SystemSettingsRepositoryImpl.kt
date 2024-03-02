package ru.weatherclock.adg.app.data.repository.settings.implementation

import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.SystemConfig
import ru.weatherclock.adg.app.data.dto.orDefault
import ru.weatherclock.adg.app.data.repository.settings.SystemSettingsRepository

class SystemSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>) :
    SystemSettingsRepository {

    override suspend fun getConfig(): SystemConfig {
        return appSettings.get().orDefault().systemConfig
    }

    override suspend fun saveConfig(callback: SystemConfig.() -> SystemConfig) {
        appSettings.update {
            val s = it.orDefault()
            s.copy(systemConfig = callback(s.systemConfig))
        }
    }
}