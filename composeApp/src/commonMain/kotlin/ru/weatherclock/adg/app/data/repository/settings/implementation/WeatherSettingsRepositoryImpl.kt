package ru.weatherclock.adg.app.data.repository.settings.implementation

import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import ru.weatherclock.adg.app.data.WeatherUnits
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.WeatherApiLanguage
import ru.weatherclock.adg.app.data.dto.WeatherConfig
import ru.weatherclock.adg.app.data.dto.orDefault
import ru.weatherclock.adg.app.data.repository.settings.WeatherSettingsRepository

class WeatherSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>):
    WeatherSettingsRepository {

    override suspend fun getConfig(): WeatherConfig = appSettings.get().orDefault().weatherConfig

    override suspend fun saveConfig(callback: WeatherConfig.() -> WeatherConfig) {
        appSettings.update {
            val s = it.orDefault()
            s.copy(weatherConfig = callback(s.weatherConfig))
        }
    }

    override val config: Flow<WeatherConfig>
        get() = appSettings.updates.mapLatest { it.orDefault().weatherConfig }

    override suspend fun isWeatherEnabled(): Boolean {
        return getConfig().weatherEnabled
    }

    override suspend fun setWeatherEnabled(enabled: Boolean) {
        saveConfig { copy(weatherEnabled = enabled) }
    }

    override suspend fun getApiKeys(): List<String> {
        return getConfig().weatherApiKeys
    }

    override suspend fun setApiKeys(apiKeys: List<String>) {
        saveConfig {
            copy(weatherApiKeys = apiKeys)
        }
    }

    override suspend fun getCityKey(): String {
        return getConfig().city.key
    }

    override suspend fun setCityKey(cityKey: String) {
        saveConfig {
            copy(
                city = city.copy(
                    key = cityKey
                )
            )
        }
    }

    override suspend fun getWeatherLanguage(): WeatherApiLanguage {
        return getConfig().weatherApiLanguage
    }

    override suspend fun setWeatherLanguage(language: WeatherApiLanguage) {
        saveConfig {
            copy(weatherApiLanguage = language)
        }
    }

    override suspend fun getLatitude(): Double {
        return getConfig().city.latitude
    }

    override suspend fun setLatitude(lat: Double) {
        saveConfig {
            copy(
                city = city.copy(
                    latitude = lat
                )
            )
        }
    }

    override suspend fun getLongitude(): Double {
        return getConfig().city.longitude
    }

    override suspend fun setLongitude(lon: Double) {
        saveConfig {
            copy(
                city = city.copy(
                    longitude = lon
                )
            )
        }
    }

    override suspend fun getUnitType(): WeatherUnits {
        return getConfig().units
    }

    override suspend fun setUnitType(type: WeatherUnits) {
        saveConfig {
            copy(units = type)
        }
    }

}