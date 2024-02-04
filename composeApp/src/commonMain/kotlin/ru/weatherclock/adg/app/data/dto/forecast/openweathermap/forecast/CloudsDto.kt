package ru.weatherclock.adg.app.data.dto.forecast.openweathermap.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CloudsDto(

    //Облачность, %
    @SerialName("all")
    val all: Int = 0
)
