package ru.weatherclock.adg.platformSpecific

import kotlinx.serialization.json.Json
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import okio.Path.Companion.toPath
import ru.weatherclock.adg.app.domain.model.settings.AppSettings

actual val appSettingsKStore: KStore<AppSettings> by lazy {
    storeOf(
        file = "${appStorage}/weather_settings.json".toPath(),
        default = AppSettings(),
        json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = true
        }
    )
}