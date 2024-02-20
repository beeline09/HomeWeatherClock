package ru.weatherclock.adg.app.data.remote.query.openweathermap.location

import ru.weatherclock.adg.app.data.remote.query.openweathermap.location.base.CoordinatesQuery

class CoordinatesByPostalCode(
    private val zipCode: String,
    private val appId: String
): CoordinatesQuery() {

    override fun endpoint(): String {
        return "zip"
    }

    override fun queryParams(): Map<String, String> {
        return mapOf(
            "appid" to appId,
            "zip" to zipCode
        )
    }
}