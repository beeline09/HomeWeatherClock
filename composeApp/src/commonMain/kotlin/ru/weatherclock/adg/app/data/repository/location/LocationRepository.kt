package ru.weatherclock.adg.app.data.repository.location

import ru.weatherclock.adg.app.data.dto.CityConfig

interface LocationRepository {

    suspend fun autocompleteSearch(
        query: String, apiKeys: List<String>, language: String
    ): List<CityConfig>
}