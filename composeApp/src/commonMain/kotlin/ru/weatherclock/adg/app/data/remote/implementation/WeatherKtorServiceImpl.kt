package ru.weatherclock.adg.app.data.remote.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import ru.weatherclock.adg.app.data.dto.ForecastDto
import ru.weatherclock.adg.app.data.remote.Endpoints
import ru.weatherclock.adg.app.data.remote.WeatherKtorService
import ru.weatherclock.adg.app.data.remote.getUrl

class WeatherKtorServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
): WeatherKtorService() {

    private val apiKeys = listOf(
        "GSWo67YCWgJ6raZqsluqkuhxsl2zJAOK",
        "JZz9kz4ElQp8VVKLiF3KVSpDtyllS7CC"
    )

    override suspend fun getWeatherForecast(forecastKey: String): ForecastDto =
        httpClient
            .get(
                baseUrl + Endpoints.WEATHER_FORECAST.getUrl(
                    forecastKey,
                    apiKeys.random(),
                    "ru-ru"
                )
            )
            .body()
}
