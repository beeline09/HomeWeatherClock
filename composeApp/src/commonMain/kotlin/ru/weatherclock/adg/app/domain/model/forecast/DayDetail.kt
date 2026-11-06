package ru.weatherclock.adg.app.domain.model.forecast

import ru.weatherclock.adg.app.data.dto.WeatherConfig
import ru.weatherclock.adg.app.data.dto.WeatherServer

data class DayDetail(
    val temperature: Double = 0.0,
    val icon: String = "",
    val iconPhrase: String? = null
)

fun DayDetail.iconUrl(weatherConfig: WeatherConfig): String =
    when (weatherConfig.server) {
        WeatherServer.Accuweather -> "https://vortex.accuweather.com/adc2010/images/slate/icons/${icon}.svg"
        // Цветные векторные иконки Meteocons (MIT) через Iconify API.
        // OWM официальный CDN отдаёт только PNG (растр).
        WeatherServer.OpenWeatherMap ->
            "https://api.iconify.design/meteocons/${icon.toMeteoconsIcon()}.svg?height=256"
    }

private fun String.toMeteoconsIcon(): String = when (this) {
    "01d" -> "clear-day-fill"
    "01n" -> "clear-night-fill"
    "02d" -> "partly-cloudy-day-fill"
    "02n" -> "partly-cloudy-night-fill"
    "03d", "03n" -> "cloudy-fill"
    "04d", "04n" -> "overcast-fill"
    "09d", "09n" -> "rain-fill"
    "10d" -> "partly-cloudy-day-rain-fill"
    "10n" -> "partly-cloudy-night-rain-fill"
    "11d" -> "thunderstorms-day-fill"
    "11n" -> "thunderstorms-night-fill"
    "13d", "13n" -> "snow-fill"
    "50d" -> "fog-day-fill"
    "50n" -> "fog-night-fill"
    else -> "cloudy-fill"
}

/*fun DayDetail.accuweatherPngIconUrl(): String =
    "https://vortex.accuweather.com/adc2010/images/slate/icons/${icon}.svg"*/
