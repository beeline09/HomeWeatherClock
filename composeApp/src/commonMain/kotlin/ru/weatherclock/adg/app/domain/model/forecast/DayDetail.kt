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
        WeatherServer.OpenWeatherMap -> "https://openweathermap.org/img/wn/${icon}@2x.png"
    }

/*fun DayDetail.accuweatherPngIconUrl(): String =
    "https://vortex.accuweather.com/adc2010/images/slate/icons/${icon}.svg"*/
