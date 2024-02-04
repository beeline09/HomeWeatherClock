package ru.weatherclock.adg.app.data.remote.query.openweathermap.forecast.base

import io.ktor.http.encodeURLQueryComponent
import ru.weatherclock.adg.app.data.remote.query.openweathermap.base.OpenWeatherMapQuery

abstract class ForecastsQuery: OpenWeatherMapQuery() {

    abstract fun endpoint(): String

    abstract fun buildQueryParams(): Map<String, String>

    final override fun buildOpenWeatherMapUrl(): String = buildString {
        val endpoint = endpoint()
        val params = buildQueryParams()
        append("data/2.5/forecast")
        if (endpoint.isNotBlank()) {
            if (endpoint.first() != '/') {
                append("/")
            }
            append(endpoint)
        }
        if (params.isNotEmpty()) {
            val keys = params.keys
            append("?")
            keys.forEachIndexed { index, param ->
                append(param)
                append("=")
                append(params[param].orEmpty().encodeURLQueryComponent())
                if (index < keys.size - 1) {
                    append("&")
                }
            }
        }
    }
}