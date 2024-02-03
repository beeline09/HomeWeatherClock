package ru.weatherclock.adg.app.data.dto.openweathermap.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.util.UNSPECIFIED_DATE

@Serializable
data class ForecastItemDto(
    @SerialName("dt")
    val timeStamp: Long = UNSPECIFIED_DATE,

    @SerialName("main")
    val main: MainDto = MainDto(),

    @SerialName("weather")
    val weather: WeatherDto = WeatherDto(),

    @SerialName("clouds")
    val clouds: CloudsDto? = null,

    @SerialName("wind")
    val wind: WindDto? = null,

    //Средняя видимость, метры. Максимальное значение видимости 10км.
    @SerialName("visibility")
    val visibility: Int = 0,

    //Вероятность осадков. Значения параметра варьируются от 0 до 1, где 0 равен 0%, 1 равен 100%
    @SerialName("pop")
    val probabilityOfPrecipitation: Double = 0.0,

    )
