package ru.weatherclock.adg.app.data.remote.query.accuweather.locations.base

abstract class CityLocationsQuery(
    apiKey: String,
    language: String
): LocationsQuery(
    apiKey,
    language
) {

    abstract fun buildCitiesUrl(): String

    final override fun buildLocationsUrl(): String {
        return "cities/${buildCitiesUrl()}"
    }
}