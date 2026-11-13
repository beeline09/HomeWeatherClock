package ru.weatherclock.adg.app.data.repository.settings

import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.dto.orDefault

/**
 * Потокобезопасный доступ к файлу настроек.
 *
 * kstore 0.7.1 пишет файл НЕатомарно (truncate → write): параллельные
 * записи (миграция ключей + freshness-метки) оставляют окно, в котором
 * файл пуст — конкурентный читатель ловил SerializationException и
 * приложение падало. Здесь все чтения/записи сериализуются одним
 * мьютексом, а битый/пустой файл мягко сбрасывается в дефолт.
 */
private val settingsMutex = Mutex()

suspend fun KStore<AppSettings>.getSettings(): AppSettings = settingsMutex.withLock {
    try {
        get().orDefault()
    } catch (e: Exception) {
        println("Settings file corrupted (${e.message}), resetting to defaults")
        AppSettings().also { defaults ->
            runCatching { set(defaults) }
        }
    }
}

/**
 * Атомарный (в рамках мьютекса) read-modify-write: transform применяется
 * к АКТУАЛЬНОМУ состоянию под локом (не к заранее прочитанному).
 */
suspend fun KStore<AppSettings>.updateSettings(
    transform: (AppSettings) -> AppSettings
): AppSettings = settingsMutex.withLock {
    val current = try {
        get().orDefault()
    } catch (e: Exception) {
        println("Settings file corrupted (${e.message}), rewriting from defaults")
        AppSettings()
    }
    val updated = transform(current)
    set(updated)
    updated
}
