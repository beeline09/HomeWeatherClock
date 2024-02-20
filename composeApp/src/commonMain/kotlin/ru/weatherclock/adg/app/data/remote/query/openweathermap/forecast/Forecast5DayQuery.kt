package ru.weatherclock.adg.app.data.remote.query.openweathermap.forecast

import ru.weatherclock.adg.app.data.WeatherUnits
import ru.weatherclock.adg.app.data.remote.query.openweathermap.forecast.base.ForecastsQuery
import ru.weatherclock.adg.app.presentation.components.text.limitDecimals

open class Forecast5DayQuery(
    private val latitude: Double,
    private val longitude: Double,
    private val appId: String,
    private val units: WeatherUnits,
    private val language: String
): ForecastsQuery() {

    override fun endpoint(): String = ""

    final override fun buildQueryParams(): Map<String, String> = buildMap {
        require(language.length >= 2)
        require(appId.isNotBlank())
        put(
            "lat",
            latitude.limitDecimals(maxDecimals = 2)
        )
        put(
            "lon",
            longitude.limitDecimals(maxDecimals = 2)
        )
        put(
            "appid",
            appId
        )
        put(
            "units",
            when (units) {
                WeatherUnits.Standard -> "standard"
                WeatherUnits.Imperial -> "imperial"
                WeatherUnits.Metric -> "metric"
            }
        )
        put(
            "lang",
            language.substring(
                0,
                2
            )
        )
    }
}