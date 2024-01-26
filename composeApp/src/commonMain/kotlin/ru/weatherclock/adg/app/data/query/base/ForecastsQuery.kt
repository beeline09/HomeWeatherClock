package ru.weatherclock.adg.app.data.query.base

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