package ru.weatherclock.adg.app.data.remote.implementation

import io.ktor.client.call.body
import ru.weatherclock.adg.app.data.dto.ForecastDto
import ru.weatherclock.adg.app.data.query.Daily5DaysForecastQuery
import ru.weatherclock.adg.app.data.remote.AppHttpClient
import ru.weatherclock.adg.app.data.remote.WeatherKtorService
import ru.weatherclock.adg.app.data.remote.error.NoWeatherApiKeyException

class WeatherKtorServiceImpl(private val httpClient: AppHttpClient): WeatherKtorService() {

    override suspend fun getWeatherForecast(
        cityKey: String,
        apiKeys: List<String>,
        language: String
    ): ForecastDto {
        if (apiKeys.isEmpty()) {
            throw NoWeatherApiKeyException()
        }
        return httpClient.get(
            Daily5DaysForecastQuery(
                apiKey = apiKeys.random(),
                cityKey = cityKey,
                language = language
            )
        ).body()
    }
}
