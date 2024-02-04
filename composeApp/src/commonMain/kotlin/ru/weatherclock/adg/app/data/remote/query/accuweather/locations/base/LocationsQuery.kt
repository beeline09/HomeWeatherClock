package ru.weatherclock.adg.app.data.remote.query.accuweather.locations.base

import ru.weatherclock.adg.app.data.remote.query.accuweather.base.AccuweatherQuery

abstract class LocationsQuery(
    apiKey: String,
    language: String
): AccuweatherQuery(
    apiKey,
    language
) {

    abstract fun buildLocationsUrl(): String

    final override fun accuweatherService(): String {
        return "locations/v1/${buildLocationsUrl()}"
    }
}