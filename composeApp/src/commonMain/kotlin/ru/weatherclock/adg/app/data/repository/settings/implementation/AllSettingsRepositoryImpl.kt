package ru.weatherclock.adg.app.data.repository.settings.implementation

import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.data.repository.settings.AllSettingsRepository
import ru.weatherclock.adg.app.domain.model.settings.AppSettings
import ru.weatherclock.adg.app.domain.model.settings.orDefault

class AllSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>):
    AllSettingsRepository {

    override suspend fun getAllSettings(): AppSettings {
        return appSettings.get().orDefault()
    }
}