package ru.weatherclock.adg.app.data

import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.serialization.WeatherUnitsSerializer

@Serializable(with = WeatherUnitsSerializer::class)
enum class WeatherUnits {

    Standard,
    Imperial,
    Metric
}
