package ru.weatherclock.adg.app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.WeatherUnits
import ru.weatherclock.adg.app.data.serialization.ColorThemeSerializer
import ru.weatherclock.adg.app.data.serialization.WeatherApiLanguageSerializer
import ru.weatherclock.adg.app.data.util.isInHours
import ru.weatherclock.adg.platformSpecific.PlatformHelper.systemLocale

@Serializable
data class AppSettings(
    val weatherConfig: WeatherConfig = WeatherConfig(),
    val calendarConfig: CalendarConfig = CalendarConfig(),
    val timeConfig: TimeConfig = TimeConfig(),
    val uiConfig: UiConfig = UiConfig(),
    val systemConfig: SystemConfig = SystemConfig(),
)

@Serializable
data class SystemConfig(
    val autoStartEnabled: Boolean = false
)

@Serializable
data class TimeConfig(
    val dotsFlashEnabled: Boolean = true,
    val dotsFlashAnimated: Boolean = true,
    val hourWithLeadingZero: Boolean = true,
    val hourlyBeepEnabled: Boolean = true,
    val hourlyBeepStartHour: Int = 9,
    val hourlyBeepEndHour: Int = 22,
)

@Serializable
enum class WeatherServer {

    @SerialName("accuweather")
    Accuweather,

    @SerialName("openweathermap")
    OpenWeatherMap
}

@Serializable
data class CityConfig(
    @SerialName("latitude") val latitude: Double = 44.6062079,
    @SerialName("longitude") val longitude: Double = 40.104053,
    @SerialName("name") val name: String = "Майкоп",
    @SerialName("region") val region: String = "Республика Адыгея",
    @SerialName("country") val country: String = "Россия",
    @SerialName("key") val key: String = "291658",
    val localizedName: String? = null,
) {
    fun getNames(): String = buildString {
        when {
            localizedName.isNullOrBlank() && name.isBlank() -> return ""
            !localizedName.isNullOrBlank() -> return localizedName
            name.isNotBlank() -> return name
            else -> {
                append(localizedName)
                append(" (")
                append(name)
                append(")")
            }
        }
    }
}

@Serializable
data class WeatherConfig(
    @SerialName("enabled") val weatherEnabled: Boolean = true,

    @SerialName("server") val server: WeatherServer = WeatherServer.Accuweather,

    @SerialName("city") val city: CityConfig = CityConfig(),

    @SerialName("language") val weatherApiLanguage: WeatherApiLanguage = WeatherApiLanguage.System,

    @SerialName("apiKeys") val weatherApiKeys: List<String> = listOf(
        "GSWo67YCWgJ6raZqsluqkuhxsl2zJAOK",
        "JZz9kz4ElQp8VVKLiF3KVSpDtyllS7CC",
//        "8f064b7cdab87c6cde5acd33b4d24a44" //Openweathermap
    ),

    @SerialName("units") val units: WeatherUnits = WeatherUnits.Metric,
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

@Serializable(with = ColorThemeSerializer::class)
enum class ColorTheme {

    Day, Night, System
}

@Serializable(with = WeatherApiLanguageSerializer::class)
enum class WeatherApiLanguage(val code: String) {

    Russian("ru-ru"), English("en-us"), System(systemLocale)
}

@Serializable
data class UiConfig(
    val colorTheme: ColorTheme = ColorTheme.System,
    val hideStartHour: Int = 23,
    val hideEndHour: Int = 8,
    val isWeatherHidden: Boolean = false,
    val isTextCalendarHidden: Boolean = true,
    val isGridCalendarHidden: Boolean = true,
)

fun AppSettings?.orDefault(): AppSettings {
    return this ?: AppSettings()
}

fun WeatherConfig.isAvailableToShow(): Boolean {
    if (weatherApiKeys.isEmpty()) return false
    if (server == WeatherServer.Accuweather && city.key.isBlank()) return false
    if (server == WeatherServer.OpenWeatherMap) {
        if (city.latitude == 0.0 && city.longitude == 0.0) return false
    }
    return weatherEnabled
}

fun ProdCalendarConfig.isAvailableToShow(): Boolean {
    return isRussia && russiaRegion > 0
}

val UiConfig.isHideElementsByTimeEnabled: Boolean
    get() = isTextCalendarHidden || isGridCalendarHidden || isWeatherHidden

fun UiConfig.isHourInRangeForHide(hour: Int): Boolean =
    isHideElementsByTimeEnabled && hour.isInHours(
        hideStartHour to hideEndHour
    )