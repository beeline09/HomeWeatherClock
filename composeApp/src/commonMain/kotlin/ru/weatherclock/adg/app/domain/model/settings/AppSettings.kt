package ru.weatherclock.adg.app.domain.model.settings

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
data class WeatherConfig(
    val weatherEnabled: Boolean = true,
    val weatherApiKeys: List<String> = listOf(
        "GSWo67YCWgJ6raZqsluqkuhxsl2zJAOK",
        "JZz9kz4ElQp8VVKLiF3KVSpDtyllS7CC"
    ),
    val weatherCityKey: String = "291658",
    val weatherApiLanguage: WeatherApiLanguage = WeatherApiLanguage.System,
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

object ColorThemeSerializer: KSerializer<ColorTheme> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "ColorTheme",
        PrimitiveKind.STRING
    )

    override fun serialize(
        encoder: Encoder,
        value: ColorTheme
    ) {
        val string = value.name
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): ColorTheme {
        val string = decoder.decodeString()
        return ColorTheme.valueOf(string)
    }
}

@Serializable(with = ColorThemeSerializer::class)
enum class ColorTheme {

    Day,
    Night,
    System
}

object WeatherApiLanguageSerializer: KSerializer<WeatherApiLanguage> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "WeatherApiLanguage",
        PrimitiveKind.STRING
    )

    override fun serialize(
        encoder: Encoder,
        value: WeatherApiLanguage
    ) {
        val string = value.name
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): WeatherApiLanguage {
        val string = decoder.decodeString()
        return WeatherApiLanguage.valueOf(string)
    }
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
    if (weatherApiKeys.isEmpty()) return false
    if (weatherCityKey.isBlank()) return false
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