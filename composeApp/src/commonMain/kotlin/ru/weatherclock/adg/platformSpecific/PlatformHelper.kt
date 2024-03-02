package ru.weatherclock.adg.platformSpecific

import androidx.compose.runtime.Composable
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import io.github.xxfast.kstore.KStore
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineDispatcher
import ru.weatherclock.adg.app.data.dto.AppSettings

internal expect object PlatformHelper {

    val fsHelper: FsHelper
    val appSettings: KStore<AppSettings>
    val defaultHttpClientEngine: HttpClientEngine
    val ioDispatcher: CoroutineDispatcher
    val systemLocale: String
    fun createDbDriver(
        dbName: String,
        schema: SqlSchema<QueryResult.Value<Unit>>
    ): SqlDriver

    @Composable
    fun SystemAppearance(isDark: Boolean)
}