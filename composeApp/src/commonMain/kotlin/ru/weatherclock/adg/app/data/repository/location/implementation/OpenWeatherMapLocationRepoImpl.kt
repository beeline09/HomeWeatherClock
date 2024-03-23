package ru.weatherclock.adg.app.data.repository.location.implementation

import ru.weatherclock.adg.app.data.dto.CityConfig
import ru.weatherclock.adg.app.data.remote.service.implementation.OpenWeatherMapKtorServiceImpl
import ru.weatherclock.adg.app.data.repository.location.LocationRepository

class OpenWeatherMapLocationRepoImpl(private val ktorService: OpenWeatherMapKtorServiceImpl) :
    LocationRepository {

    override suspend fun autocompleteSearch(
        query: String, apiKeys: List<String>, language: String
    ): List<CityConfig> {
        return ktorService.autoCompleteSearch(
            query = query,
            apiKeys = apiKeys
        ).map {
            val localizedName = it.localName[language.substring(0, 2)].orEmpty()
            CityConfig(
                localizedName = localizedName,
                name = it.name,
                region = it.state.orEmpty(),
                country = it.country,
                latitude = it.latitude,
                longitude = it.longitude,
                key = ""
            )
        }
    }
}