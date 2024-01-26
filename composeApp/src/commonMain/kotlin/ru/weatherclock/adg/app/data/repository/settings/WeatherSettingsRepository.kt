package ru.weatherclock.adg.app.data.repository.settings

import kotlinx.coroutines.flow.Flow
import ru.weatherclock.adg.app.domain.model.settings.WeatherConfig

interface WeatherSettingsRepository {

    val config: Flow<WeatherConfig>
    suspend fun getConfig(): WeatherConfig

    suspend fun saveConfig(callback: WeatherConfig.() -> WeatherConfig)

    suspend fun isWeatherEnabled(): Boolean
    suspend fun setWeatherEnabled(enabled: Boolean)

    suspend fun getApiKeys(): List<String>
    suspend fun setApiKeys(apiKeys: List<String>)

    suspend fun getCityKey(): String
    suspend fun setCityKey(cityKey: String)

    suspend fun getWeatherLanguage(): String
    suspend fun setWeatherLanguage(language: String)

}