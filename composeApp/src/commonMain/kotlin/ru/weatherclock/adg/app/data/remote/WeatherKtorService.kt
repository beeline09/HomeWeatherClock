package ru.weatherclock.adg.app.data.remote

import ru.weatherclock.adg.app.data.dto.accuweather.AccuweatherForecastDto
import ru.weatherclock.adg.app.data.remote.error.NoWeatherApiKeyException
import ru.weatherclock.adg.app.data.util.CircularIterator

abstract class WeatherKtorService {

    abstract suspend fun getWeatherForecast(
        cityKey: String,
        apiKeys: List<String>,
        language: String
    ): AccuweatherForecastDto

    protected fun checkAndGetApikey(apiKeys: List<String>): String {
        if (apiKeys.isEmpty()) {
            throw NoWeatherApiKeyException()
        }
        if (!apiKeysIterator.hasNext()) {
            apiKeysIterator = CircularIterator(apiKeys)
        }
        return apiKeysIterator.next().takeIf { apiKeysIterator.hasNext() }
            ?: error("API Keys is empty!")
    }

    private var apiKeysIterator = CircularIterator<String>(emptyList())
}
