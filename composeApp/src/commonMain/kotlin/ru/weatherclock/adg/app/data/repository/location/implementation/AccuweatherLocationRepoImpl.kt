package ru.weatherclock.adg.app.data.repository.location.implementation

import ru.weatherclock.adg.app.data.dto.CityConfig
import ru.weatherclock.adg.app.data.remote.service.implementation.AccuweatherKtorServiceImpl
import ru.weatherclock.adg.app.data.repository.location.LocationRepository

class AccuweatherLocationRepoImpl(private val ktorService: AccuweatherKtorServiceImpl) :
    LocationRepository {

    override suspend fun autocompleteSearch(
        query: String, apiKeys: List<String>, language: String
    ): List<CityConfig> {
        return ktorService.autoCompleteSearch(
            query = query,
            apiKeys = apiKeys,
            language = language
        ).map {
            CityConfig(
                localizedName = it.localizedName,
                name = "",
                key = it.key,
                region = it.administrativeArea.localizedName,
                country = it.country.localizedName,
                latitude = 0.0,
                longitude = 0.0
            )
        }
    }
}