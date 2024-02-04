package ru.weatherclock.adg.app.data.repository.settings.implementation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.data.WeatherUnits
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.WeatherApiLanguage
import ru.weatherclock.adg.app.data.dto.WeatherConfig
import ru.weatherclock.adg.app.data.dto.WeatherConfigData
import ru.weatherclock.adg.app.data.dto.orDefault
import ru.weatherclock.adg.app.data.repository.settings.WeatherSettingsRepository

@OptIn(ExperimentalCoroutinesApi::class)
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
        return getConfig().weatherConfig.weatherApiKeys
    }

    override suspend fun setApiKeys(apiKeys: List<String>) {
        saveConfig {
            when (weatherConfig) {
                is WeatherConfigData.Accuweather -> copy(
                    weatherConfig = weatherConfig.copy(weatherApiKeys = apiKeys)
                )

                is WeatherConfigData.OpenWeatherMap -> copy(
                    weatherConfig = weatherConfig.copy(weatherApiKeys = apiKeys)
                )
            }
        }
    }

    override suspend fun getCityKey(): String {
        return when (val c = getConfig().weatherConfig) {
            is WeatherConfigData.Accuweather -> c.cityKey
            is WeatherConfigData.OpenWeatherMap -> ""
        }
    }

    override suspend fun setCityKey(cityKey: String) {
        saveConfig {
            when (weatherConfig) {
                is WeatherConfigData.Accuweather -> copy(
                    weatherConfig = weatherConfig.copy(
                        cityKey = cityKey
                    )
                )

                is WeatherConfigData.OpenWeatherMap -> this
            }
        }
    }

    override suspend fun getWeatherLanguage(): WeatherApiLanguage {
        return getConfig().weatherConfig.weatherApiLanguage
    }

    override suspend fun setWeatherLanguage(language: WeatherApiLanguage) {
        saveConfig {
            when (weatherConfig) {
                is WeatherConfigData.Accuweather -> copy(
                    weatherConfig = weatherConfig.copy(
                        weatherApiLanguage = language
                    )
                )

                is WeatherConfigData.OpenWeatherMap -> copy(
                    weatherConfig = weatherConfig.copy(
                        weatherApiLanguage = language
                    )
                )
            }
        }
    }

    override suspend fun getLatitude(): Double {
        return when (val c = getConfig().weatherConfig) {
            is WeatherConfigData.Accuweather -> 0.0
            is WeatherConfigData.OpenWeatherMap -> c.latitude
        }
    }

    override suspend fun setLatitude(lat: Double) {
        saveConfig {
            when (weatherConfig) {
                is WeatherConfigData.Accuweather -> this

                is WeatherConfigData.OpenWeatherMap -> copy(
                    weatherConfig = weatherConfig.copy(
                        latitude = lat
                    )
                )
            }
        }
    }

    override suspend fun getLongitude(): Double {
        return when (val c = getConfig().weatherConfig) {
            is WeatherConfigData.Accuweather -> 0.0
            is WeatherConfigData.OpenWeatherMap -> c.longitude
        }
    }

    override suspend fun setLongitude(lon: Double) {
        saveConfig {
            when (weatherConfig) {
                is WeatherConfigData.Accuweather -> this

                is WeatherConfigData.OpenWeatherMap -> copy(
                    weatherConfig = weatherConfig.copy(
                        longitude = lon
                    )
                )
            }
        }
    }

    override suspend fun getUnitType(): WeatherUnits {
        return getConfig().weatherConfig.units
    }

    override suspend fun setUnitType(type: WeatherUnits) {
        saveConfig {
            when (weatherConfig) {
                is WeatherConfigData.Accuweather -> copy(
                    weatherConfig = weatherConfig.copy(
                        units = type
                    )
                )

                is WeatherConfigData.OpenWeatherMap -> copy(
                    weatherConfig = weatherConfig.copy(
                        units = type
                    )
                )
            }
        }
    }

}