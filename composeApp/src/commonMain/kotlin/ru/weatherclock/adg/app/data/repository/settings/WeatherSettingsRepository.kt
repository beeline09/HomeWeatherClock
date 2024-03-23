package ru.weatherclock.adg.app.data.repository.settings

import kotlinx.coroutines.flow.Flow
import ru.weatherclock.adg.app.data.WeatherUnits
import ru.weatherclock.adg.app.data.dto.WeatherApiLanguage
import ru.weatherclock.adg.app.data.dto.WeatherConfig
import ru.weatherclock.adg.app.data.dto.WeatherServer

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

    suspend fun getWeatherLanguage(): WeatherApiLanguage
    suspend fun setWeatherLanguage(language: WeatherApiLanguage)

    suspend fun getLatitude(): Double
    suspend fun setLatitude(lat: Double)

    suspend fun getLongitude(): Double
    suspend fun setLongitude(lon: Double)

    suspend fun getUnitType(): WeatherUnits
    suspend fun setUnitType(type: WeatherUnits)

    suspend fun getWeatherServer(): WeatherServer
    suspend fun setWeatherServer(server: WeatherServer)

}