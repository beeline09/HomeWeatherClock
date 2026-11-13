package ru.weatherclock.adg.platformSpecific

import androidx.compose.runtime.Composable
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.apache5.Apache5
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import ru.weatherclock.adg.app.data.dto.AppSettings
import java.io.File
import java.io.File.separatorChar
import java.util.Properties

internal actual object PlatformHelper {
    actual val fsHelper: FsHelper
        get() = FsHelper()

    actual val appSettings: KStore<AppSettings> by lazy {
        storeOf(
            file = "${fsHelper.appStoragePath}${separatorChar}weather_settings.json".toPath(),
            default = AppSettings(),
            enableCache = false,
            json = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                prettyPrint = true
            }
        )
    }
    actual val defaultHttpClientEngine: HttpClientEngine by lazy {
        Apache5.create()
    }

    actual val defaultWeatherApiKey: String by lazy {
        // gradlew run запускается из каталога модуля; прямые запуски — из корня
        sequenceOf(File("../local.properties"), File("local.properties"))
            .firstOrNull { it.isFile }
            ?.let { f ->
                runCatching {
                    java.util.Properties().apply { f.inputStream().use { load(it) } }
                }.getOrNull()
            }
            ?.getProperty("weatherApiKey")
            .orEmpty()
    }
    actual val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO
    actual val systemLocale: String by lazy {
        val lang: String = System.getProperty("user.language")
//        val country: String = System.getProperty("user.country")
        "${lang}-${lang}".lowercase()
    }

    actual fun createDbDriver(
        dbName: String,
        schema: SqlSchema<QueryResult.Value<Unit>>
    ): SqlDriver {
        val dirPath = "${fsHelper.appStoragePath}${fsHelper.separatorChar}"
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return JdbcSqliteDriver(
            url = "jdbc:sqlite:${dirPath}$dbName",
            properties = Properties().apply {
                put(
                    "foreign_keys",
                    "true"
                )
            }).also { driver ->
            // JDBC-драйвер не ведёт схему сам (в отличие от Android/Native):
            // user_version храним и мигрируем явно. Раньше был только
            // schema.create() — на существующей БД миграции не применялись
            val currentVersion = driver.executeQuery(
                identifier = null,
                sql = "PRAGMA user_version",
                mapper = { cursor ->
                    QueryResult.Value(if (cursor.next().value) (cursor.getLong(0) ?: 0L) else 0L)
                },
                parameters = 0
            ).value
            val targetVersion = schema.version ?: 0L
            when {
                currentVersion == 0L -> schema.create(driver)
                currentVersion < targetVersion -> schema.migrate(
                    driver, currentVersion, targetVersion
                )
            }
            if (currentVersion != targetVersion) {
                driver.execute(null, "PRAGMA user_version = $targetVersion", 0)
            }
        }
    }

    @Composable
    actual fun SystemAppearance(isDark: Boolean) {
    }
}