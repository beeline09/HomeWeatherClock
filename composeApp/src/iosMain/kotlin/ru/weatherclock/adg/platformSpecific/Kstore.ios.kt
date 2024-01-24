package ru.weatherclock.adg.platformSpecific

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import ru.weatherclock.adg.app.domain.model.AppSettings

actual val appSettingsKStore: KStore<AppSettings> by lazy {
    storeOf(
        "${appStorage}${separatorChar}weather_settings.json",
        AppSettings()
    )
}