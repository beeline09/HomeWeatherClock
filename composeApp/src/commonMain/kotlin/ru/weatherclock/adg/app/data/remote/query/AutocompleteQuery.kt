package ru.weatherclock.adg.app.data.remote.query

import io.ktor.http.encodeURLQueryComponent
import ru.weatherclock.adg.app.data.remote.query.accuweather.locations.CityLocationsQuery

class AutocompleteQuery(
    apiKey: String,
    language: String,
    private val searchQuery: String
): CityLocationsQuery(
    apiKey,
    language
) {

    override fun buildCitiesUrl(): String {
        return "autocomplete"
    }

    override fun queryParams(): String {
        return "q=${searchQuery.encodeURLQueryComponent()}"
    }
}