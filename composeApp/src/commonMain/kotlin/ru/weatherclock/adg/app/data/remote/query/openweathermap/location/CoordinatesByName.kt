package ru.weatherclock.adg.app.data.remote.query.openweathermap.location

import ru.weatherclock.adg.app.data.remote.query.openweathermap.location.base.CoordinatesQuery

class CoordinatesByName(
    private val query: String,
    private val appId: String
): CoordinatesQuery() {

    override fun endpoint(): String {
        return "direct"
    }

    override fun queryParams(): Map<String, String> {
        return mapOf(
            "appid" to appId,
            "q" to query
        )
    }
}