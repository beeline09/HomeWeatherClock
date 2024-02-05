package ru.weatherclock.adg.app.data.dto.forecast.openweathermap.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.db.OpenWeatherMap.WeatherIcon

@Serializable
data class WeatherDto(
    @SerialName("id")
    val id: Int = 0,

    @SerialName("main")
    val main: String = "",

    @SerialName("description")
    val description: String? = null,

    @SerialName("icon")
    val icon: String = ""
)

fun WeatherDto.asDbModel(forecastPid: Long): WeatherIcon {
    return WeatherIcon(
        forecast_pid = forecastPid,
        icon = icon,
        description = description?.replaceFirstChar { it.uppercaseChar() },
        main = main,
        id = id
    )
}
