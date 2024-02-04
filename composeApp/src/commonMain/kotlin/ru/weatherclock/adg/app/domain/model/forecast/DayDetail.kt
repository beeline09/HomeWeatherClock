package ru.weatherclock.adg.app.domain.model.forecast

import ru.weatherclock.adg.app.data.dto.WeatherConfigData

data class DayDetail(
    val temperature: Double = 0.0,
    val icon: String = "",
    val iconPhrase: String? = null
)

fun DayDetail.iconUrl(weatherConfigData: WeatherConfigData): String =
    when (weatherConfigData) {
        is WeatherConfigData.Accuweather -> "https://vortex.accuweather.com/adc2010/images/slate/icons/${icon}.svg"
        is WeatherConfigData.OpenWeatherMap -> "https://openweathermap.org/img/wn/${icon}@2x.png"
    }

/*fun DayDetail.accuweatherPngIconUrl(): String =
    "https://vortex.accuweather.com/adc2010/images/slate/icons/${icon}.svg"*/
