package ru.weatherclock.adg.app.data.remote.query.accuweather.forecast.base

import ru.weatherclock.adg.app.data.remote.query.accuweather.base.AccuweatherQuery

abstract class ForecastsQuery(
    apiKey: String,
    language: String
): AccuweatherQuery(
    apiKey,
    language
) {

    abstract fun endpoint(): String

    final override fun accuweatherService(): String {
        return "forecasts/v1/${endpoint()}"
    }
}