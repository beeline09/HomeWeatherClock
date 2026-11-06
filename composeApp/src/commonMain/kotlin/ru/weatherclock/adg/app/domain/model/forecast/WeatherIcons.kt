package ru.weatherclock.adg.app.domain.model.forecast

import ru.weatherclock.adg.app.data.dto.WeatherIconSet
import ru.weatherclock.adg.app.data.dto.WeatherServer

/**
 * Семантика погодного условия — промежуточный слой между кодами провайдеров
 * (OWM "10d", AccuWeather "12") и именами иконок в наборах.
 */
internal enum class WeatherSemantic {
    CLEAR_DAY, CLEAR_NIGHT,
    PARTLY_CLOUDY_DAY, PARTLY_CLOUDY_NIGHT,
    CLOUDY, OVERCAST,
    FOG_DAY, FOG_NIGHT,
    SHOWERS,
    RAIN_DAY, RAIN_NIGHT, OVERCAST_RAIN,
    THUNDER_DAY, THUNDER_NIGHT,
    SNOW, OVERCAST_SNOW, SLEET,
    HAZE_DAY, HAZE_NIGHT,
    WIND, HOT, COLD
}

private fun owmSemantic(code: String): WeatherSemantic? = when (code) {
    "01d" -> WeatherSemantic.CLEAR_DAY
    "01n" -> WeatherSemantic.CLEAR_NIGHT
    "02d" -> WeatherSemantic.PARTLY_CLOUDY_DAY
    "02n" -> WeatherSemantic.PARTLY_CLOUDY_NIGHT
    "03d", "03n" -> WeatherSemantic.CLOUDY
    "04d", "04n" -> WeatherSemantic.OVERCAST
    "09d", "09n" -> WeatherSemantic.SHOWERS
    "10d" -> WeatherSemantic.RAIN_DAY
    "10n" -> WeatherSemantic.RAIN_NIGHT
    "11d" -> WeatherSemantic.THUNDER_DAY
    "11n" -> WeatherSemantic.THUNDER_NIGHT
    "13d", "13n" -> WeatherSemantic.SNOW
    "50d" -> WeatherSemantic.FOG_DAY
    "50n" -> WeatherSemantic.FOG_NIGHT
    else -> null
}

/**
 * Официальная таблица AccuWeather (developer.accuweather.com/documentation/weather-icons).
 * Коды 9, 10, 27, 28 не используются; 33–44 — ночные варианты.
 */
private fun accuSemantic(code: String): WeatherSemantic? = when (code) {
    "1", "2" -> WeatherSemantic.CLEAR_DAY
    "3" -> WeatherSemantic.PARTLY_CLOUDY_DAY
    "4", "6", "7" -> WeatherSemantic.CLOUDY
    "5" -> WeatherSemantic.HAZE_DAY
    "8" -> WeatherSemantic.OVERCAST
    "11" -> WeatherSemantic.FOG_DAY
    "12" -> WeatherSemantic.SHOWERS
    "13", "40" -> WeatherSemantic.OVERCAST_RAIN
    "14", "18" -> WeatherSemantic.RAIN_DAY
    "15", "16", "17" -> WeatherSemantic.THUNDER_DAY
    "19", "20", "21", "22", "43" -> WeatherSemantic.SNOW
    "23", "44" -> WeatherSemantic.OVERCAST_SNOW
    "24", "25", "26", "29" -> WeatherSemantic.SLEET
    "30" -> WeatherSemantic.HOT
    "31" -> WeatherSemantic.COLD
    "32" -> WeatherSemantic.WIND
    "33", "34" -> WeatherSemantic.CLEAR_NIGHT
    "35" -> WeatherSemantic.PARTLY_CLOUDY_NIGHT
    "36", "38" -> WeatherSemantic.CLOUDY
    "37" -> WeatherSemantic.HAZE_NIGHT
    "39" -> WeatherSemantic.RAIN_NIGHT
    "41", "42" -> WeatherSemantic.THUNDER_NIGHT
    else -> null
}

private fun meteoconsName(s: WeatherSemantic): String = when (s) {
    WeatherSemantic.CLEAR_DAY -> "clear-day"
    WeatherSemantic.CLEAR_NIGHT -> "clear-night"
    WeatherSemantic.PARTLY_CLOUDY_DAY -> "partly-cloudy-day"
    WeatherSemantic.PARTLY_CLOUDY_NIGHT -> "partly-cloudy-night"
    WeatherSemantic.CLOUDY -> "cloudy"
    WeatherSemantic.OVERCAST -> "overcast"
    WeatherSemantic.FOG_DAY -> "fog-day"
    WeatherSemantic.FOG_NIGHT -> "fog-night"
    WeatherSemantic.SHOWERS -> "rain"
    WeatherSemantic.RAIN_DAY -> "partly-cloudy-day-rain"
    WeatherSemantic.RAIN_NIGHT -> "partly-cloudy-night-rain"
    WeatherSemantic.OVERCAST_RAIN -> "rain"
    WeatherSemantic.THUNDER_DAY -> "thunderstorms-day"
    WeatherSemantic.THUNDER_NIGHT -> "thunderstorms-night"
    WeatherSemantic.SNOW -> "snow"
    WeatherSemantic.OVERCAST_SNOW -> "snow"
    WeatherSemantic.SLEET -> "sleet"
    WeatherSemantic.HAZE_DAY -> "haze-day"
    WeatherSemantic.HAZE_NIGHT -> "haze-night"
    WeatherSemantic.WIND -> "wind"
    WeatherSemantic.HOT -> "thermometer-warmer"
    WeatherSemantic.COLD -> "thermometer-colder"
}

private fun twemojiName(s: WeatherSemantic): String = when (s) {
    WeatherSemantic.CLEAR_DAY -> "2600"
    WeatherSemantic.CLEAR_NIGHT -> "1f319"
    WeatherSemantic.PARTLY_CLOUDY_DAY -> "26c5"
    WeatherSemantic.PARTLY_CLOUDY_NIGHT -> "1f319"
    WeatherSemantic.CLOUDY -> "2601"
    WeatherSemantic.OVERCAST -> "1f325"
    WeatherSemantic.FOG_DAY, WeatherSemantic.FOG_NIGHT -> "1f32b"
    WeatherSemantic.SHOWERS -> "1f327"
    WeatherSemantic.RAIN_DAY -> "1f326"
    WeatherSemantic.RAIN_NIGHT, WeatherSemantic.OVERCAST_RAIN -> "1f327"
    WeatherSemantic.THUNDER_DAY, WeatherSemantic.THUNDER_NIGHT -> "26c8"
    WeatherSemantic.SNOW, WeatherSemantic.OVERCAST_SNOW, WeatherSemantic.SLEET -> "1f328"
    WeatherSemantic.HAZE_DAY, WeatherSemantic.HAZE_NIGHT -> "1f32b"
    WeatherSemantic.WIND -> "1f4a8"
    WeatherSemantic.HOT -> "1f975"
    WeatherSemantic.COLD -> "1f976"
}

private fun wiName(s: WeatherSemantic): String = when (s) {
    WeatherSemantic.CLEAR_DAY -> "day-sunny"
    WeatherSemantic.CLEAR_NIGHT -> "night-clear"
    WeatherSemantic.PARTLY_CLOUDY_DAY -> "day-cloudy"
    WeatherSemantic.PARTLY_CLOUDY_NIGHT -> "night-alt-cloudy"
    WeatherSemantic.CLOUDY, WeatherSemantic.OVERCAST -> "cloudy"
    WeatherSemantic.FOG_DAY -> "day-fog"
    WeatherSemantic.FOG_NIGHT -> "fog"
    WeatherSemantic.SHOWERS -> "showers"
    WeatherSemantic.RAIN_DAY -> "day-rain"
    WeatherSemantic.RAIN_NIGHT -> "night-alt-rain"
    WeatherSemantic.OVERCAST_RAIN -> "rain"
    WeatherSemantic.THUNDER_DAY -> "day-thunderstorm"
    WeatherSemantic.THUNDER_NIGHT -> "night-alt-thunderstorm"
    WeatherSemantic.SNOW, WeatherSemantic.OVERCAST_SNOW -> "snow"
    WeatherSemantic.SLEET -> "sleet"
    WeatherSemantic.HAZE_DAY -> "day-haze"
    WeatherSemantic.HAZE_NIGHT -> "dust"
    WeatherSemantic.WIND -> "strong-wind"
    WeatherSemantic.HOT -> "hot"
    WeatherSemantic.COLD -> "snowflake-cold"
}

private fun WeatherIconSet.resourceDir(): String? = when (this) {
    WeatherIconSet.MeteoconsFill -> "meteocons-fill"
    WeatherIconSet.MeteoconsLine -> "meteocons"
    WeatherIconSet.Twemoji -> "twemoji"
    WeatherIconSet.WiMono -> "wi"
    WeatherIconSet.Official -> null
}

/** Публичный SHA тега v2.0.0 репозитория Meteocons (для сетевого fallback). */
private const val METEOCONS_TAG_SHA = "fdd19749b9"

private fun WeatherIconSet.iconifyName(s: WeatherSemantic): String = when (this) {
    WeatherIconSet.MeteoconsFill -> meteoconsName(s) + "-fill"
    WeatherIconSet.MeteoconsLine -> meteoconsName(s)
    WeatherIconSet.Twemoji -> twemojiName(s)
    WeatherIconSet.WiMono -> wiName(s)
    WeatherIconSet.Official -> meteoconsName(s) + "-fill"
}

private fun semanticOf(server: WeatherServer, code: String): WeatherSemantic? =
    if (server == WeatherServer.OpenWeatherMap) owmSemantic(code) else accuSemantic(code)

/**
 * Резолвер источника иконки погоды.
 *
 * Приоритет: bundled SVG из ресурсов приложения; если набора в ресурсах нет
 * или код незнаком провайдеру — сеть (Iconify для SVG-наборов, официальный
 * CDN OpenWeatherMap для [WeatherIconSet.Official]).
 *
 * Миграция настроек: поле WeatherConfig.iconSet добавлено с дефолтом
 * (MeteoconsFill) — файлы настроек старых версий читаются без потерь,
 * apiKeys/сервер/город не затрагиваются.
 */
object WeatherIconResolver {

    sealed interface Source {
        /** SVG из composeResources, путь вида files/weather_icons/<set>/<prov>-<код>.svg */
        data class BundledSvg(val resourcePath: String) : Source

        /** URL сети (Iconify SVG / официальный PNG OpenWeatherMap) */
        data class Network(val url: String) : Source
    }

    /** Локальный источник, если он применим для сочетания набор+сервер+код. */
    fun bundled(
        set: WeatherIconSet,
        server: WeatherServer,
        code: String
    ): Source.BundledSvg? {
        val effectiveSet = if (set == WeatherIconSet.Official) {
            // Официальный CDN AccuWeather мёртв — используем bundled Meteocons
            if (server == WeatherServer.OpenWeatherMap) return null
            WeatherIconSet.MeteoconsFill
        } else set
        val dir = effectiveSet.resourceDir() ?: return null
        val prefix = if (server == WeatherServer.OpenWeatherMap) "owm" else "accu"
        return Source.BundledSvg("files/weather_icons/$dir/$prefix-$code.svg")
    }

    /** Сетевой источник (fallback для незнакомого кода или выбора Official). */
    fun network(
        set: WeatherIconSet,
        server: WeatherServer,
        code: String
    ): Source.Network {
        if (set == WeatherIconSet.Official && server == WeatherServer.OpenWeatherMap) {
            return Source.Network("https://openweathermap.org/img/wn/$code@4x.png")
        }
        val semantic = semanticOf(server, code) ?: WeatherSemantic.CLOUDY
        val effectiveSet = if (set == WeatherIconSet.Official) WeatherIconSet.MeteoconsFill else set
        // Iconify-тела meteocons содержат <symbol>/<use>, которые SkSVG не
        // отрисовывает — сетевой fallback для meteocons идёт на production-файлы
        if (effectiveSet == WeatherIconSet.MeteoconsFill ||
            effectiveSet == WeatherIconSet.MeteoconsLine
        ) {
            val sub = if (effectiveSet == WeatherIconSet.MeteoconsFill) "fill" else "line"
            val dir = if (server == WeatherServer.OpenWeatherMap) {
                "openweathermap/$code"
            } else {
                "all/${meteoconsName(semantic)}"
            }
            return Source.Network(
                "https://raw.githubusercontent.com/basmilius/meteocons/$METEOCONS_TAG_SHA/" +
                    "production/$sub/$dir.svg"
            )
        }
        // остались только Twemoji и WiMono — плоские path-тела, SkSVG рендерит
        val prefix = when (effectiveSet) {
            WeatherIconSet.Twemoji -> "twemoji"
            else -> "wi"
        }
        return Source.Network(
            "https://api.iconify.design/$prefix/" +
                "${effectiveSet.iconifyName(semantic)}.svg?height=256"
        )
    }
}
