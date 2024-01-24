package ru.weatherclock.adg.platformSpecific

import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.domain.model.AppSettings

expect val appSettingsKStore: KStore<AppSettings>