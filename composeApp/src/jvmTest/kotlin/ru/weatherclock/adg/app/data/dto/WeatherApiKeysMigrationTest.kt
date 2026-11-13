package ru.weatherclock.adg.app.data.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Раздельные ключи провайдеров + миграция legacy-списка:
 * старый общий список (apiKeys) трактуется как ключи OpenWeatherMap
 * (исторически туда попадал ключ из local.properties), Accuweather —
 * только собственный список. Сохранение ключей гасит legacy.
 */
class WeatherApiKeysMigrationTest {

    @Test
    fun `legacy список достаётся OpenWeatherMap и не достаётся Accuweather`() {
        // openWeatherMapApiKeys явно пуст: дефолт этого поля на jvm несёт
        // ключ из local.properties (окружение!), тестируем именно fallback
        val config = WeatherConfig(
            weatherApiKeys = listOf("owm-key"),
            openWeatherMapApiKeys = emptyList()
        )
        assertEquals(listOf("owm-key"), config.apiKeysFor(WeatherServer.OpenWeatherMap))
        assertEquals(emptyList(), config.apiKeysFor(WeatherServer.Accuweather))
    }

    @Test
    fun `собственные списки приоритетнее legacy`() {
        val config = WeatherConfig(
            weatherApiKeys = listOf("legacy"),
            openWeatherMapApiKeys = listOf("own-owm"),
            accuweatherApiKeys = listOf("own-accu"),
        )
        assertEquals(listOf("own-owm"), config.apiKeysFor(WeatherServer.OpenWeatherMap))
        assertEquals(listOf("own-accu"), config.apiKeysFor(WeatherServer.Accuweather))
    }

    @Test
    fun `isAvailableToShow требует ключи активного сервера`() {
        // Ключ есть только у OWM (legacy), активен OWM — доступно
        val owm = WeatherConfig(
            server = WeatherServer.OpenWeatherMap,
            weatherApiKeys = listOf("k"),
            openWeatherMapApiKeys = emptyList(),
            city = CityConfig(latitude = 44.6, longitude = 40.1)
        )
        assertTrue(owm.isAvailableToShow())
        // Тот же конфиг, но активен Accuweather: своих ключей нет, cityKey пуст
        val accu = owm.copy(server = WeatherServer.Accuweather)
        assertFalse(accu.isAvailableToShow())
    }

    @Test
    fun `старый файл настроек без новых полей читается без потерь`() {
        // Сериализация: отсутствие accuweatherApiKeys/openWeatherMapApiKeys
        // даёт пустой список Accuweather и читает legacy (openWeatherMapApiKeys
        // при отсутствии поля зависит от defaultWeatherApiKey платформы —
        // в jvm-тесте он непустой, поэтому fallback проверяем на явном конфиге)
        val json = """{"enabled":true,"apiKeys":["k1"],"server":"openweathermap"}"""
        val config = kotlinx.serialization.json.Json.decodeFromString<WeatherConfig>(json)
        assertEquals(listOf("k1"), config.weatherApiKeys)
        assertEquals(emptyList(), config.accuweatherApiKeys)
    }
}
