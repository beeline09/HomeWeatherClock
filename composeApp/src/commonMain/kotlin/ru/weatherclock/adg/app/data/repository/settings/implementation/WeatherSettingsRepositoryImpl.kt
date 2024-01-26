package ru.weatherclock.adg.app.data.repository.settings.implementation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import io.github.xxfast.kstore.KStore
import ru.weatherclock.adg.app.data.repository.settings.WeatherSettingsRepository
import ru.weatherclock.adg.app.domain.model.settings.AppSettings
import ru.weatherclock.adg.app.domain.model.settings.WeatherConfig
import ru.weatherclock.adg.app.domain.model.settings.orDefault

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
        return getConfig().weatherApiKeys
    }

    override suspend fun setApiKeys(apiKeys: List<String>) {
        saveConfig { copy(weatherApiKeys = apiKeys) }
    }

    override suspend fun getCityKey(): String {
        return getConfig().weatherCityKey
    }

    override suspend fun setCityKey(cityKey: String) {
        saveConfig { copy(weatherCityKey = cityKey) }
    }

    override suspend fun getWeatherLanguage(): String {
        return getConfig().weatherLanguage
    }

    override suspend fun setWeatherLanguage(language: String) {
        saveConfig { copy(weatherLanguage = language) }
    }

}