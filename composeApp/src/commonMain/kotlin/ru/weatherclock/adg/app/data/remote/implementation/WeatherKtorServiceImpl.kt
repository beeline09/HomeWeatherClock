package ru.weatherclock.adg.app.data.remote.implementation

import io.ktor.client.call.body
import ru.weatherclock.adg.app.data.dto.accuweather.AccuweatherForecastDto
import ru.weatherclock.adg.app.data.query.Daily5DaysForecastQuery
import ru.weatherclock.adg.app.data.remote.AppHttpClient
import ru.weatherclock.adg.app.data.remote.WeatherKtorService

class WeatherKtorServiceImpl(private val httpClient: AppHttpClient): WeatherKtorService() {

    override suspend fun getWeatherForecast(
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
