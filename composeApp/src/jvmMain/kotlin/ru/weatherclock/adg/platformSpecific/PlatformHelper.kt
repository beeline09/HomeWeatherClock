package ru.weatherclock.adg.platformSpecific

import androidx.compose.runtime.Composable
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.apache.Apache
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
        Apache.create()
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
        return JdbcSqliteDriver(url = "jdbc:sqlite:${dirPath}$dbName",
            properties = Properties().apply {
                put(
                    "foreign_keys",
                    "true"
                )
            }).also {
            schema.create(it)
        }
    }

    @Composable
    actual fun SystemAppearance(isDark: Boolean) {
    }
}