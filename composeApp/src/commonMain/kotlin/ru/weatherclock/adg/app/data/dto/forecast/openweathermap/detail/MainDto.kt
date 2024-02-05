package ru.weatherclock.adg.app.data.dto.forecast.openweathermap.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.db.OpenWeatherMap.MainInfo

@Serializable
data class MainDto(
    //Температура. Единица измерения по умолчанию: Кельвин, Метрическая система: Цельсий, Британская система мер: Фаренгейт.
    @SerialName("temp")
    val temperature: Double = Double.MIN_VALUE,

    //Этот температурный параметр отвечает за восприятие человеком погоды. Единица измерения по умолчанию: Кельвин, Метрическая система: Цельсий, Британская система: Фаренгейт.
    @SerialName("feels_like")
    val feelsLike: Double = Double.MIN_VALUE,

    //Минимальная температура на момент расчета. Это минимальная прогнозируемая температура (в пределах крупных мегаполисов и городских территорий), используйте этот параметр по желанию.
    @SerialName("temp_min")
    val minTemperature: Double = Double.MIN_VALUE,

    //Максимальная температура на момент расчета. Это максимальная прогнозируемая температура (в пределах крупных мегаполисов и городских территорий), используйте этот параметр по желанию.
    @SerialName("temp_max")
    val maxTemperature: Double = Double.MIN_VALUE,

    //Атмосферное давление на уровне моря по умолчанию, гПа
    @SerialName("pressure")
    val pressure: Int = 0,

    //Атмосферное давление на уровне моря, гПа
    @SerialName("sea_level")
    val seaLevel: Int = 0,

    //Атмосферное давление на уровне земли, гПа
    @SerialName("grnd_level")
    val groundLevel: Int = 0,

    //Влажность, %
    @SerialName("humidity")
    val humidity: Int = 0
)

fun MainDto.asDbModel(forecastPid: Long): MainInfo {
    return MainInfo(
        forecast_pid = forecastPid,
        temperature = temperature,
        feels_like = feelsLike,
        ground_level = groundLevel,
        seaLevel = seaLevel,
        humidity = humidity,
        max_temperature = maxTemperature,
        min_temperature = minTemperature,
        pressure = pressure
    )
}
