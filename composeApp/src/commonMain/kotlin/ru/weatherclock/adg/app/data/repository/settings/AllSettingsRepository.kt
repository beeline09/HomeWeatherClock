package ru.weatherclock.adg.app.data.repository.settings

import ru.weatherclock.adg.app.domain.model.settings.AppSettings

interface AllSettingsRepository {

    suspend fun getAllSettings(): AppSettings
}