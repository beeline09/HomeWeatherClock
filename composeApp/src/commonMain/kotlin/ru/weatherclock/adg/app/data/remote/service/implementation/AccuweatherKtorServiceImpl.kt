package ru.weatherclock.adg.app.data.remote.service.implementation

import io.ktor.client.call.body
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.AccuweatherForecastDto
import ru.weatherclock.adg.app.data.remote.AppHttpClient
import ru.weatherclock.adg.app.data.remote.query.accuweather.forecast.Daily5DaysForecastQuery
import ru.weatherclock.adg.app.data.remote.service.WeatherKtorService

class AccuweatherKtorServiceImpl(private val httpClient: AppHttpClient): WeatherKtorService() {

    suspend fun getWeatherForecast(
        cityKey: String,
        apiKeys: List<String>,
        language: String
    ): AccuweatherForecastDto {
        return httpClient.get(
            Daily5DaysForecastQuery(
                apiKey = checkAndGetApikey(apiKeys = apiKeys),
                cityKey = cityKey,
                language = language
            )
        ).body()
    }
}
