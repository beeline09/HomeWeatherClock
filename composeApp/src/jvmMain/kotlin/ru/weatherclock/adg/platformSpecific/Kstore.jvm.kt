package ru.weatherclock.adg.platformSpecific

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import ru.weatherclock.adg.app.domain.model.WeatherSettings

actual val weatherSettingsKStore: KStore<WeatherSettings> by lazy {
    storeOf(
        "${appStorage}${separatorChar}weather_settings.json",
        WeatherSettings()
    )
}