package ru.weatherclock.adg.app.domain.model.forecast

import ru.weatherclock.adg.db.RealFeelTemperature
import ru.weatherclock.adg.db.RealFeelTemperatureShade
import ru.weatherclock.adg.db.Temperature

data class Temperature(

    /**
     * Минимальная температура
     */
    val minimum: UnitInfo,

    /**
     * Максимальная температура
     */
    val maximum: UnitInfo
)

fun Temperature.asDomainModel(): ru.weatherclock.adg.app.domain.model.forecast.Temperature {
    return Temperature(
        minimum = UnitInfo(
            value = min_value,
            unit = min_unit,
            unitType = min_unit_type,
            phrase = min_phrase
        ),
        maximum = UnitInfo(
            value = max_value,
            unit = max_unit,
            unitType = max_unit_type,
            phrase = max_phrase
        )
    )
}

fun RealFeelTemperature.asDomainModel(): ru.weatherclock.adg.app.domain.model.forecast.Temperature {
    return Temperature(
        minimum = UnitInfo(
            value = min_value,
            unit = min_unit,
            unitType = min_unit_type,
            phrase = min_phrase
        ),
        maximum = UnitInfo(
            value = max_value,
            unit = max_unit,
            unitType = max_unit_type,
            phrase = max_phrase
        )
    )
}

fun RealFeelTemperatureShade.asDomainModel(): ru.weatherclock.adg.app.domain.model.forecast.Temperature {
    return Temperature(
        minimum = UnitInfo(
            value = min_value,
            unit = min_unit,
            unitType = min_unit_type,
            phrase = min_phrase
        ),
        maximum = UnitInfo(
            value = max_value,
            unit = max_unit,
            unitType = max_unit_type,
            phrase = max_phrase
        )
    )
}

fun ru.weatherclock.adg.app.domain.model.forecast.Temperature.asDbTemperature(forecastPid: Long): Temperature {
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

fun ru.weatherclock.adg.app.domain.model.forecast.Temperature.asDbRealFeelTemperature(forecastPid: Long): RealFeelTemperature {
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

fun ru.weatherclock.adg.app.domain.model.forecast.Temperature.asDbRealFeelTemperatureShade(forecastPid: Long): RealFeelTemperatureShade {
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