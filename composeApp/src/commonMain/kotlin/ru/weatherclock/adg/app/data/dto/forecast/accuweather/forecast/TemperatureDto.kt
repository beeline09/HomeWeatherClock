package ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.db.Accuweather.RealFeelTemperature
import ru.weatherclock.adg.db.Accuweather.RealFeelTemperatureShade
import ru.weatherclock.adg.db.Accuweather.Temperature

@Serializable
data class TemperatureDto(

    /**
     * Минимальная температура
     */
    @SerialName("Minimum")
    val minimum: UnitDto = UnitDto(),

    /**
     * Максимальная температура
     */
    @SerialName("Maximum")
    val maximum: UnitDto = UnitDto()
)

fun TemperatureDto.asAccuweatherDbTemperature(forecastPid: Long): Temperature {
    return Temperature(
        min_value = minimum.value,
        min_phrase = minimum.phrase,
        min_unit = minimum.unit,
        min_unit_type = minimum.unitType,
        max_value = maximum.value,
        max_phrase = maximum.phrase,
        max_unit = maximum.unit,
        max_unit_type = maximum.unitType,
        forecast_pid = forecastPid,
        pid = -1L
    )
}

fun TemperatureDto.asAccuweatherDbRealFeelTemperature(forecastPid: Long): RealFeelTemperature {
    return RealFeelTemperature(
        min_value = minimum.value,
        min_phrase = minimum.phrase,
        min_unit = minimum.unit,
        min_unit_type = minimum.unitType,
        max_value = maximum.value,
        max_phrase = maximum.phrase,
        max_unit = maximum.unit,
        max_unit_type = maximum.unitType,
        forecast_pid = forecastPid,
        pid = -1L
    )
}

fun TemperatureDto.asAccuweatherDbRealFeelTemperatureShade(forecastPid: Long): RealFeelTemperatureShade {
    return RealFeelTemperatureShade(
        min_value = minimum.value,
        min_phrase = minimum.phrase,
        min_unit = minimum.unit,
        min_unit_type = minimum.unitType,
        max_value = maximum.value,
        max_phrase = maximum.phrase,
        max_unit = maximum.unit,
        max_unit_type = maximum.unitType,
        forecast_pid = forecastPid,
        pid = -1L
    )
}