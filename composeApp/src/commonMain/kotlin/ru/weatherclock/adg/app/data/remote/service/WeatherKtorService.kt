package ru.weatherclock.adg.app.data.remote.service

import ru.weatherclock.adg.app.data.error.NoWeatherApiKeyException
import ru.weatherclock.adg.app.data.util.CircularIterator

abstract class WeatherKtorService {

    protected fun checkAndGetApikey(apiKeys: List<String>): String {
        if (apiKeys.isEmpty()) {
            throw NoWeatherApiKeyException()
        }
        if (!apiKeysIterator.hasNext()) {
            apiKeysIterator = CircularIterator(apiKeys)
        }
        return apiKeysIterator.next()
    }

    private var apiKeysIterator = CircularIterator<String>(emptyList())
}
