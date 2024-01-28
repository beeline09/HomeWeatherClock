package ru.weatherclock.adg.platformSpecific

import kotlinx.serialization.json.Json
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import ru.weatherclock.adg.app.domain.model.settings.AppSettings

actual val appSettingsKStore: KStore<AppSettings> by lazy {
    storeOf(
        filePath = "${appStorage}${separatorChar}weather_settings.json",
        default = AppSettings(),
        json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = true
        }
    )
}