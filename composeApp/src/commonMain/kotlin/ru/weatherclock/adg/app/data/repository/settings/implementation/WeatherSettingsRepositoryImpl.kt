package ru.weatherclock.adg.app.data.repository.settings.implementation

import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import ru.weatherclock.adg.app.data.WeatherUnits
import ru.weatherclock.adg.app.data.dto.*
import ru.weatherclock.adg.app.data.repository.settings.WeatherSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.getSettings
import ru.weatherclock.adg.app.data.repository.settings.updateSettings
import ru.weatherclock.adg.platformSpecific.PlatformHelper.defaultWeatherApiKey

class WeatherSettingsRepositoryImpl(private val appSettings: KStore<AppSettings>) :
    WeatherSettingsRepository {

    override suspend fun getConfig(): WeatherConfig {
        migrateLegacyApiKeysIfNeeded()
        return appSettings.getSettings().weatherConfig
    }

    /**
     * Одноразовая миграция ключей старого формата (единый список apiKeys):
     * вводился для АКТИВНОГО сервера → переносится в его собственный список,
     * legacy гасится (идемпотентно: пустой legacy = миграция уже выполнена).
     * Плюс первый старт без ключей: подкладывается платформенный дефолт
     * (jvm — из local.properties) как ключ OpenWeatherMap.
     *
     * transform применяется к АКТУАЛЬНОМУ состоянию под мьютексом
     * (см. updateSettings): параллельные вызовы не портят файл.
     */
    private suspend fun migrateLegacyApiKeysIfNeeded() {
        val config = appSettings.getSettings().weatherConfig
        val legacy = config.weatherApiKeys
        val targetEmpty = when (config.server) {
            WeatherServer.Accuweather -> config.accuweatherApiKeys.isEmpty()
            WeatherServer.OpenWeatherMap -> config.openWeatherMapApiKeys.isEmpty()
        }
        val needLegacyMigration = legacy.isNotEmpty() && targetEmpty
        val needDefaultSeed = legacy.isEmpty() && config.accuweatherApiKeys.isEmpty() &&
                config.openWeatherMapApiKeys.isEmpty() && defaultWeatherApiKey.isNotBlank()
        if (!needLegacyMigration && !needDefaultSeed) return
        appSettings.updateSettings { s ->
            val cfg = s.weatherConfig
            // Повторная проверка ПОД мьютексом от актуального состояния:
            // если другой поток уже выполнил миграцию — не трогаем ничего
            val targetEmpty = when (cfg.server) {
                WeatherServer.Accuweather -> cfg.accuweatherApiKeys.isEmpty()
                WeatherServer.OpenWeatherMap -> cfg.openWeatherMapApiKeys.isEmpty()
            }
            val keys: List<String>? = when {
                cfg.weatherApiKeys.isNotEmpty() && targetEmpty -> cfg.weatherApiKeys
                cfg.weatherApiKeys.isEmpty() && cfg.accuweatherApiKeys.isEmpty() &&
                        cfg.openWeatherMapApiKeys.isEmpty() &&
                        defaultWeatherApiKey.isNotBlank() -> listOf(defaultWeatherApiKey)

                else -> null
            }
            if (keys == null) {
                s
            } else {
                s.copy(
                    weatherConfig = when (cfg.server) {
                        WeatherServer.Accuweather -> cfg.copy(
                            accuweatherApiKeys = keys,
                            weatherApiKeys = emptyList()
                        )

                        WeatherServer.OpenWeatherMap -> cfg.copy(
                            openWeatherMapApiKeys = keys,
                            weatherApiKeys = emptyList()
                        )
                    }
                )
            }
        }
    }

    override suspend fun saveConfig(callback: WeatherConfig.() -> WeatherConfig) {
        appSettings.updateSettings { s ->
            s.copy(weatherConfig = callback(s.weatherConfig))
        }
    }

    override suspend fun getFetchState(): WeatherFetchState =
        appSettings.getSettings().weatherFetchState

    override suspend fun setFetchState(state: WeatherFetchState) {
        appSettings.updateSettings { s ->
            s.copy(weatherFetchState = state)
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
        return getConfig().let { it.apiKeysFor(it.server) }
    }

    override suspend fun setApiKeys(apiKeys: List<String>) {
        saveConfig {
            copy(weatherApiKeys = apiKeys)
        }
    }

    override suspend fun getApiKeys(server: WeatherServer): List<String> {
        return getConfig().apiKeysFor(server)
    }

    override suspend fun setApiKeys(server: WeatherServer, apiKeys: List<String>) {
        saveConfig {
            copy(
                accuweatherApiKeys = if (server == WeatherServer.Accuweather) {
                    apiKeys
                } else {
                    accuweatherApiKeys
                },
                openWeatherMapApiKeys = if (server == WeatherServer.OpenWeatherMap) {
                    apiKeys
                } else {
                    openWeatherMapApiKeys
                },
                // legacy-список погашен: пустой список провайдера больше
                // не подменится старыми ключами
                weatherApiKeys = emptyList()
            )
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

    override suspend fun getWeatherServer(): WeatherServer {
        return getConfig().server
    }

    override suspend fun setWeatherServer(server: WeatherServer) {
        saveConfig {
            copy(server = server)
        }
    }

    override suspend fun getWeatherIconSet(): WeatherIconSet {
        return getConfig().iconSet
    }

    override suspend fun setWeatherIconSet(iconSet: WeatherIconSet) {
        saveConfig {
            copy(iconSet = iconSet)
        }
    }

}