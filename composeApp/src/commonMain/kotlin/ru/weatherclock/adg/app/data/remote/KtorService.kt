package ru.weatherclock.adg.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import ru.weatherclock.adg.app.data.dto.ForecastDto

class KtorService(
    private val httpClient: HttpClient,
    private val baseUrl: String
):
    AbstractKtorService() {

    private val apiKey = "GSWo67YCWgJ6raZqsluqkuhxsl2zJAOK"

    override suspend fun getWeatherForecast(forecastKey: String): ForecastDto =
        httpClient
            .get(
                baseUrl + Endpoints.WEATHER_FORECAST.getUrl(
                    forecastKey,
                    apiKey
                )
            )
            .body()
}
