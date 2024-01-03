package ru.weatherclock.adg.platformSpecific

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import ru.weatherclock.adg.app.domain.model.WeatherSettings

actual val store: KStore<WeatherSettings> by lazy {
    storeOf(
        "${appStorage}/weather_settings.json",
        WeatherSettings()
    )
}