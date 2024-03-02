package ru.weatherclock.adg.app.data.repository.settings

import ru.weatherclock.adg.app.data.dto.SystemConfig

interface SystemSettingsRepository {

    suspend fun getConfig(): SystemConfig
    suspend fun saveConfig(callback: SystemConfig.() -> SystemConfig)

}