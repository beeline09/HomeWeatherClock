package ru.weatherclock.adg.app.data.query.base

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