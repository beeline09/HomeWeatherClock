package ru.weatherclock.adg.app.data.remote.query.openweathermap.base

import ru.weatherclock.adg.app.data.remote.query.ApiQuery

abstract class OpenWeatherMapQuery: ApiQuery {

    abstract fun buildOpenWeatherMapUrl(): String

    final override fun buildUrl(): String = buildString {
        val url = buildOpenWeatherMapUrl()
        append("https://api.openweathermap.org")
        if (url.firstOrNull() != '/') {
            append("/")
        }
        append(url)
    }

}