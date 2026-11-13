package ru.weatherclock.adg.app.data.repository.settings

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.WeatherConfig
import ru.weatherclock.adg.app.data.dto.WeatherServer
import ru.weatherclock.adg.app.data.repository.settings.implementation.WeatherSettingsRepositoryImpl
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Миграция старого формата ключей (единый apiKeys): перенос в собственный
 * список АКТИВНОГО сервера без повторного ввода пользователем.
 * kstore 0.7.1 — класс (не интерфейс), поэтому тест на реальном
 * file-based сторе во временной папке.
 */
class LegacyApiKeysMigrationTest {

    private val tmpDir = Files.createTempDirectory("kstore_migration_test")
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    private fun newStore(initial: AppSettings): KStore<AppSettings> {
        val store: KStore<AppSettings> = storeOf(
            file = tmpDir.resolve("settings_${System.nanoTime()}.json").toString().toPath(),
            default = AppSettings(),
            json = json
        )
        runBlocking { store.set(initial) }
        return store
    }

    private fun repo(config: WeatherConfig): Pair<WeatherSettingsRepositoryImpl, KStore<AppSettings>> {
        val store = newStore(AppSettings(weatherConfig = config))
        return WeatherSettingsRepositoryImpl(store) to store
    }

    @AfterTest
    fun cleanup() {
        tmpDir.toFile().deleteRecursively()
    }

    @Test
    fun `legacy ключ мигрирует в Accuweather при активном Accuweather`() = runBlocking {
        val (repository, store) = repo(
            WeatherConfig(server = WeatherServer.Accuweather, weatherApiKeys = listOf("accu-key"))
        )
        val config = repository.getConfig()
        assertEquals(listOf("accu-key"), config.accuweatherApiKeys)
        // legacy погашен — повторной миграции не будет
        assertTrue(store.get()!!.weatherConfig.weatherApiKeys.isEmpty())
    }

    @Test
    fun `legacy ключ мигрирует в OpenWeatherMap при активном OWM`() = runBlocking {
        val (repository, store) = repo(
            WeatherConfig(server = WeatherServer.OpenWeatherMap, weatherApiKeys = listOf("owm-key"))
        )
        val config = repository.getConfig()
        assertEquals(listOf("owm-key"), config.openWeatherMapApiKeys)
        assertTrue(store.get()!!.weatherConfig.weatherApiKeys.isEmpty())
    }

    @Test
    fun `миграция идемпотентна — повторное чтение ничего не меняет`() = runBlocking {
        val (repository, _) = repo(
            WeatherConfig(server = WeatherServer.Accuweather, weatherApiKeys = listOf("k"))
        )
        val first = repository.getConfig()
        val second = repository.getConfig()
        assertEquals(first, second)
        assertEquals(listOf("k"), second.accuweatherApiKeys)
    }

    @Test
    fun `заданные новые ключи не перезаписываются legacy`() = runBlocking {
        val (repository, _) = repo(
            WeatherConfig(
                server = WeatherServer.Accuweather,
                weatherApiKeys = listOf("old"),
                accuweatherApiKeys = listOf("new")
            )
        )
        assertEquals(listOf("new"), repository.getConfig().accuweatherApiKeys)
    }
}
