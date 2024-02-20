package ru.weatherclock.adg.app.data.remote.query.accuweather.locations

import ru.weatherclock.adg.app.data.remote.query.accuweather.locations.base.LocationsQuery

class PostalCodeQuery(
    apiKey: String,
    language: String,
    private val postalCode: String
): LocationsQuery(
    apiKey,
    language
) {

    final override fun buildLocationsUrl(): String {
        return "postalcodes/search"
    }

    override fun queryParams(): Map<String, String> {
        TODO("Not yet implemented")
    }
}