package ru.weatherclock.adg.app.data.dto.forecast.openweathermap.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.db.OpenWeatherMap.Wind

@Serializable
data class WindDto(

    //Скорость ветра. Единица измерения по умолчанию: метр/сек, Метрическая система: метр/сек, Британская система: мили/час.
    @SerialName("speed")
    val speed: Double = 0.0,

    //Направление ветра, градусы (метеорологические)
    @SerialName("deg")
    val degree: Int = 0,

    //Порывы ветра. Единица измерения по умолчанию: метр/сек, Метрическая система: метр/сек, Британская система: мили/час.
    @SerialName("gust")
    val gust: Double = 0.0
)

fun WindDto.asDbModel(forecastPid: Long): Wind {
    return Wind(
        forecast_pid = forecastPid,
        speed = speed,
        degree = degree,
        gust = gust
    )
}
