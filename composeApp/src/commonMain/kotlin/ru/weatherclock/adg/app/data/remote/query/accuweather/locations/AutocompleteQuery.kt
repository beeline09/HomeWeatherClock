package ru.weatherclock.adg.app.data.remote.query.accuweather.locations

import ru.weatherclock.adg.app.data.remote.query.accuweather.locations.base.CityLocationsQuery

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

    override fun queryParams(): Map<String, String> = mapOf("q" to searchQuery)
}