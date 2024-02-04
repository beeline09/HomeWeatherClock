package ru.weatherclock.adg.app.data.repository.settings.implementation

import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.orDefault
import ru.weatherclock.adg.app.data.repository.settings.AllSettingsRepository

class AllSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>):
    AllSettingsRepository {

    override suspend fun getAllSettings(): AppSettings {
        return appSettings.get().orDefault()
    }
}