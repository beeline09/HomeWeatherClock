package ru.weatherclock.adg.app.data.remote.query.openweathermap.location.base

import io.ktor.http.encodeURLQueryComponent
import ru.weatherclock.adg.app.data.remote.query.openweathermap.base.OpenWeatherMapQuery

abstract class CoordinatesQuery: OpenWeatherMapQuery() {

    abstract fun endpoint(): String

    abstract fun queryParams(): Map<String, String>

    final override fun buildOpenWeatherMapUrl(): String = buildString {
        val endpoint = endpoint()
        val params = queryParams()
        append("geo/1.0")
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