package ru.weatherclock.adg.app.data.dto.forecast.openweathermap.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.util.epochSecondsToLocalDateTime
import ru.weatherclock.adg.app.data.util.toDbFormat
import ru.weatherclock.adg.db.OpenWeatherMap.ForecastItem

@Serializable
data class ForecastItemDto(
    @SerialName("dt")
    val timeStamp: Long,

    @SerialName("main")
    val main: MainDto = MainDto(),

    @SerialName("weather")
    val weather: List<WeatherDto> = emptyList(),

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

    @SerialName("rain")
    val rain: Map<String, Double> = emptyMap(),
    @SerialName("snow")
    val snow: Map<String, Double> = emptyMap(),

    @SerialName("sys")
    val sys: SysDto = SysDto()

)

fun ForecastItemDto.asDbModel(
    latitude: Double,
    longitude: Double
): ForecastItem {
    return ForecastItem(
        pid = -1L,
        date_time = timeStamp.epochSecondsToLocalDateTime().toDbFormat(),
        visibility = visibility,
        pop = probabilityOfPrecipitation,
        clouds = clouds?.all ?: 0,
        part_of_day = sys.partOfDay.toInt(),
        latitude = latitude,
        longitude = longitude
    )
}
