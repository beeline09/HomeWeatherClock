package ru.weatherclock.adg.app.domain.model

import kotlinx.serialization.Serializable
import ru.weatherclock.adg.platformSpecific.systemLocale

@Serializable
data class WeatherSettings(
    val russiaRegion: Int = 0,
    val isRussia: Boolean = true,
    val weatherLanguage: String = systemLocale,
    val dotsAnimated: Boolean = true,
    val weatherApiKeys: List<String> = emptyList(),
    val weatherKey: String = "",
    val textCalendarEnabled: Boolean = true,
    val gridCalendarEnabled: Boolean = true,
    val weatherEnabled: Boolean = true,
    val prodCalendarDayDescriptionEnabled: Boolean = true,
)

fun WeatherSettings?.orDefault(): WeatherSettings {
    if (this == null) return WeatherSettings()
    return this
}
