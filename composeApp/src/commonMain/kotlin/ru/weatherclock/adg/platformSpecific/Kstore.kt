package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.domain.model.settings.AppSettings
import ru.weatherclock.adg.app.domain.model.settings.orDefault

expect val appSettingsKStore: KStore<AppSettings>

fun KStore<AppSettings>.safeUpdate(callback: AppSettings.() -> AppSettings) {
    CoroutineScope(ioDispatcher).launch {
        update {
            callback(it.orDefault())
        }
    }
}