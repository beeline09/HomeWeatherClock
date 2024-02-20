package ru.weatherclock.adg.app.data.remote.query.accuweather.base

import io.ktor.http.encodeURLQueryComponent
import ru.weatherclock.adg.app.data.remote.query.ApiQuery

abstract class AccuweatherQuery(
    private val apiKey: String,
    private val language: String
): ApiQuery {

    abstract fun accuweatherService(): String

    abstract fun queryParams(): Map<String, String>

    final override fun buildUrl(): String = buildString {
        val params = queryParams()
        append("https://dataservice.accuweather.com/")
        append(accuweatherService())
        append("?")
        if (params.isNotEmpty()) {
            val keys = params.keys
            keys.forEachIndexed { index, param ->
                append(param)
                append("=")
                append(params[param].orEmpty().encodeURLQueryComponent())
                if (index < keys.size - 1) {
                    append("&")
                }
            }
            append("&")
        }
        append("apikey=")
        append(apiKey.encodeURLQueryComponent())
        append("&language=")
        append(language.encodeURLQueryComponent())
    }

}