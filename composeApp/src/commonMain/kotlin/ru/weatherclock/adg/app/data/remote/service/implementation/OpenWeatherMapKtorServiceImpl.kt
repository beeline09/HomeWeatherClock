package ru.weatherclock.adg.app.data.remote.service.implementation

import io.ktor.client.call.body
import ru.weatherclock.adg.app.data.WeatherUnits
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.OpenWeatherMapForecastDto
import ru.weatherclock.adg.app.data.remote.AppHttpClient
import ru.weatherclock.adg.app.data.remote.query.openweathermap.forecast.Forecast5DayQuery
import ru.weatherclock.adg.app.data.remote.service.WeatherKtorService

class OpenWeatherMapKtorServiceImpl(private val httpClient: AppHttpClient): WeatherKtorService() {

    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        units: WeatherUnits,
        apiKeys: List<String>,
        language: String
    ): OpenWeatherMapForecastDto {
        return httpClient.get(
            Forecast5DayQuery(
                appId = checkAndGetApikey(apiKeys = apiKeys),
                latitude = latitude,
                longitude = longitude,
                language = language,
                units = units,
            )
        ).body()
    }
}
