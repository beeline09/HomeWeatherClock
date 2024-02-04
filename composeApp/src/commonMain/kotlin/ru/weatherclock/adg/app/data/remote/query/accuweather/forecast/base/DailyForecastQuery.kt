package ru.weatherclock.adg.app.data.remote.query.accuweather.forecast.base

abstract class DailyForecastQuery(
    apiKey: String,
    language: String,
): ForecastsQuery(
    apiKey,
    language
) {

    abstract fun buildDailyForecastUrl(): String

    final override fun endpoint(): String {
        return "daily/${buildDailyForecastUrl()}"
    }
}