package ru.weatherclock.adg.app.data.query

import ru.weatherclock.adg.app.data.query.base.DailyForecastQuery

class Daily5DaysForecastQuery(
    apiKey: String,
    language: String,
    private val cityKey: String,
    private val detailed: Boolean = true,
    private val withMetrics: Boolean = true,
): DailyForecastQuery(
    apiKey,
    language
) {

    override fun queryParams(): String = buildString {
        append("details=")
        if (detailed) append("true") else append("false")
        append("&metric=")
        if (withMetrics) append("true") else append("false")
    }

    override fun buildDailyForecastUrl(): String {
        return "5day/${cityKey}"
    }
}