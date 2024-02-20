package ru.weatherclock.adg.app.data.remote.query.accuweather.forecast

import ru.weatherclock.adg.app.data.remote.query.accuweather.forecast.base.DailyForecastQuery

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

    override fun queryParams(): Map<String, String> = mapOf(
        "details" to if (detailed) "true" else "false",
        "metric" to if (withMetrics) "true" else "false"
    )

    override fun buildDailyForecastUrl(): String {
        return "5day/${cityKey}"
    }
}