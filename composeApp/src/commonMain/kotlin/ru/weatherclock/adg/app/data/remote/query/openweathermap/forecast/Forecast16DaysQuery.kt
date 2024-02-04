package ru.weatherclock.adg.app.data.remote.query.openweathermap.forecast

import ru.weatherclock.adg.app.data.WeatherUnits

class Forecast16DaysQuery(
    latitude: Double,
    longitude: Double,
    appId: String,
    units: WeatherUnits,
    language: String
): Forecast5DayQuery(
    latitude,
    longitude,
    appId,
    units,
    language
) {

    override fun endpoint(): String = "daily"
}