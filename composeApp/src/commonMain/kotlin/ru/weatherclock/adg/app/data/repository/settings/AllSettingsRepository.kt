package ru.weatherclock.adg.app.data.repository.settings

import ru.weatherclock.adg.app.data.dto.AppSettings

interface AllSettingsRepository {

    suspend fun getAllSettings(): AppSettings
}