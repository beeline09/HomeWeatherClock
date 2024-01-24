package ru.weatherclock.adg.app.domain.model

import kotlinx.serialization.Serializable
import ru.weatherclock.adg.platformSpecific.systemLocale

@Serializable
data class AppSettings(
    val weatherConfig: WeatherConfig = WeatherConfig(),
    val calendarConfig: CalendarConfig = CalendarConfig(),
    val dotsAnimated: Boolean = true,
)

@Serializable
data class WeatherConfig(
    val weatherEnabled: Boolean = true,
    val weatherApiKeys: List<String> = emptyList(),
    val weatherCityKey: String = "",
    val weatherLanguage: String = systemLocale,
)

@Serializable
data class CalendarConfig(
    val textCalendarEnabled: Boolean = true,
    val gridCalendarEnabled: Boolean = true,
    val prodCalendarConfig: ProdCalendarConfig = ProdCalendarConfig(),
)

@Serializable
data class ProdCalendarConfig(
    val isRussia: Boolean = true,
    //0 - Вся Россия
    val russiaRegion: Int = 0,
    val dayDescriptionEnabled: Boolean = true,
)

fun AppSettings?.orDefault(): AppSettings {
    if (this == null) return AppSettings()
    return this
}

fun WeatherConfig.isAvailableToShow(): Boolean {
    if (weatherApiKeys.isEmpty()) return false
    if (weatherCityKey.isBlank()) return false
    return weatherEnabled
}

fun ProdCalendarConfig.isAvailableToShow(): Boolean {
    return isRussia && russiaRegion > 0
}