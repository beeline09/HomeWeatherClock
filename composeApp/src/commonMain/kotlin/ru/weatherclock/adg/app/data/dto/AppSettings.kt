package ru.weatherclock.adg.app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.WeatherUnits
import ru.weatherclock.adg.app.data.serialization.ColorThemeSerializer
import ru.weatherclock.adg.app.data.serialization.WeatherApiLanguageSerializer
import ru.weatherclock.adg.app.data.util.isInHours
import ru.weatherclock.adg.platformSpecific.systemLocale

@Serializable
data class AppSettings(
    val weatherConfig: WeatherConfig = WeatherConfig(),
    val calendarConfig: CalendarConfig = CalendarConfig(),
    val timeConfig: TimeConfig = TimeConfig(),
    val uiConfig: UiConfig = UiConfig()
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
sealed class WeatherConfigData {

    @SerialName("language")
    abstract val weatherApiLanguage: WeatherApiLanguage

    @SerialName("apiKeys")
    abstract val weatherApiKeys: List<String>

    @SerialName("units")
    abstract val units: WeatherUnits

    @Serializable
    @SerialName("accuweather")
    data class Accuweather(
        @SerialName("cityKey")
        val cityKey: String = "291658",
        override val weatherApiLanguage: WeatherApiLanguage = WeatherApiLanguage.System,
        override val weatherApiKeys: List<String> = listOf(
            "GSWo67YCWgJ6raZqsluqkuhxsl2zJAOK",
            "JZz9kz4ElQp8VVKLiF3KVSpDtyllS7CC"
        ),
        override val units: WeatherUnits = WeatherUnits.Metric,

        ): WeatherConfigData()

    @Serializable
    @SerialName("openweathermap")
    data class OpenWeatherMap(
        @SerialName("lat")
        val latitude: Double = 44.6062079,
        @SerialName("lon")
        val longitude: Double = 40.104053,
        override val weatherApiLanguage: WeatherApiLanguage = WeatherApiLanguage.System,
        override val weatherApiKeys: List<String> = listOf("8f064b7cdab87c6cde5acd33b4d24a44"),
        override val units: WeatherUnits = WeatherUnits.Metric
    ): WeatherConfigData()
}

@Serializable
data class WeatherConfig(
    val weatherEnabled: Boolean = true,
    val weatherConfig: WeatherConfigData = WeatherConfigData.OpenWeatherMap()
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

    Day,
    Night,
    System
}

@Serializable(with = WeatherApiLanguageSerializer::class)
enum class WeatherApiLanguage(val code: String) {

    Russian("ru-ru"),
    English("en-us"),
    System(systemLocale)
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
    if (weatherConfig.weatherApiKeys.isEmpty()) return false
    if (weatherConfig is WeatherConfigData.Accuweather && weatherConfig.cityKey.isBlank()) return false
    if (weatherConfig is WeatherConfigData.OpenWeatherMap) {
        if (weatherConfig.latitude == 0.0 && weatherConfig.longitude == 0.0) return false
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