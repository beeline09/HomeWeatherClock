package ru.weatherclock.adg.app.data.repository.settings

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
 * Устойчивость к битому файлу настроек: kstore 0.7.1 пишет файл
 * неатомарно (truncate → write), поэтому конкурентный читатель может
 * увидеть пустой или повреждённый файл — getSettings обязан вернуть
 * дефолт и восстановить файл, а не упасть SerializationException.
 */
class CorruptedSettingsFileTest {

    private val tmpDir = Files.createTempDirectory("kstore_corrupted_test")
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    @AfterTest
    fun cleanup() {
        tmpDir.toFile().deleteRecursively()
    }

    private fun storeWithContent(content: String?): KStore<AppSettings> {
        val file = tmpDir.resolve("settings_${System.nanoTime()}.json").toFile()
        if (content != null) file.writeText(content)
        return storeOf(
            file = file.absolutePath.toPath(),
            default = AppSettings(),
            json = json
        )
    }

    @Test
    fun `пустой файл не роняет приложение`() = runBlocking {
        val store = storeWithContent("")
        val settings = store.getSettings()
        assertEquals(AppSettings(), settings)
    }

    @Test
    fun `мусор в файле не роняет приложение и восстанавливает его`() = runBlocking {
        val store = storeWithContent("{ битый json !!!")
        val settings = store.getSettings()
        assertEquals(AppSettings(), settings)
        // файл восстановлен дефолтом — следующий запуск читает валидно
        assertEquals(AppSettings(), store.getSettings())
    }

    @Test
    fun `конкурентные миграции не портят файл`() = runBlocking {
        // Пять параллельных читателей с legacy-ключом (сценарий краша):
        // все getConfig одновременно → миграция под мьютексом → файл валиден
        val store = storeWithContent(
            Json.encodeToString(
                AppSettings.serializer(),
                AppSettings(
                    weatherConfig = WeatherConfig(
                        server = WeatherServer.OpenWeatherMap,
                        weatherApiKeys = listOf("k")
                    )
                )
            )
        )
        val repository = WeatherSettingsRepositoryImpl(store)
        coroutineScope {
            repeat(5) {
                launch(Dispatchers.IO) {
                    val config = repository.getConfig()
                    assertTrue(config.apiKeysFor(WeatherServer.OpenWeatherMap).isNotEmpty())
                }
            }
        }
        // Файл валиден, миграция выполнена ровно один раз
        val final = store.getSettings().weatherConfig
        assertEquals(listOf("k"), final.openWeatherMapApiKeys)
        assertTrue(final.weatherApiKeys.isEmpty())
    }
}
