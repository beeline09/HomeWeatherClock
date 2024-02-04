package ru.weatherclock.adg.app.data.remote.query.accuweather.base

import ru.weatherclock.adg.app.data.remote.query.base.ApiQuery

abstract class AccuweatherQuery(
    private val apiKey: String,
    private val language: String
): ApiQuery {

    abstract fun accuweatherService(): String

    abstract fun queryParams(): String

    final override fun buildUrl(): String = buildString {
        val query = queryParams()
        append("https://dataservice.accuweather.com/")
        append(accuweatherService())
        append("?apikey=")
        append(apiKey)
        append("&language=")
        append(language)
        if (query.isNotBlank()) {
            append("&")
            append(query)
        }
    }

}