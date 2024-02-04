package ru.weatherclock.adg.app.data.dto.forecast.openweathermap.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
