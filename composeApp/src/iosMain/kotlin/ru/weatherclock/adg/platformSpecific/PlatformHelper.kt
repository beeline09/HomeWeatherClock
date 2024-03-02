package ru.weatherclock.adg.platformSpecific

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSLocale
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle
import ru.weatherclock.adg.app.data.dto.AppSettings

internal actual object PlatformHelper {

    actual val fsHelper: FsHelper by lazy { FsHelper() }

    actual val appSettings: KStore<AppSettings> by lazy {

        val paths = NSSearchPathForDirectoriesInDomains(
            NSApplicationSupportDirectory, NSUserDomainMask, true
        )
        val documentsDirectory = paths[0] as String
        val databaseDirectory = "$documentsDirectory${fsHelper.separatorChar}prefs"
        val fileManager = NSFileManager.defaultManager()
        if (!fileManager.fileExistsAtPath(databaseDirectory)) fileManager.createDirectoryAtPath(
            databaseDirectory, true, null, null
        )
        storeOf(
            file = "${databaseDirectory}${fsHelper.separatorChar}weather_settings.json".toPath(),
            default = AppSettings()
        )
    }

    actual val defaultHttpClientEngine: HttpClientEngine by lazy {
        Darwin.create()
    }

    actual val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.Default

    actual val systemLocale: String
        get() = with(NSLocale.currentLocale) {
            "${languageCode}-${languageCode}".lowercase()
        }

    actual fun createDbDriver(
        dbName: String, schema: SqlSchema<QueryResult.Value<Unit>>
    ): SqlDriver {
        return NativeSqliteDriver(
            schema = schema,
            name = dbName,
            onConfiguration = { config: DatabaseConfiguration ->
                config.copy(
                    extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true)
                )
            })
    }

    @Composable
    actual fun SystemAppearance(isDark: Boolean) {
        LaunchedEffect(isDark) {
            UIApplication.sharedApplication.setStatusBarStyle(
                if (isDark) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent
            )
        }
    }
}