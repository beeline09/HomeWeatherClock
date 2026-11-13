package ru.weatherclock.adg.platformSpecific

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.os.ConfigurationCompat
import androidx.core.view.WindowCompat
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import ru.weatherclock.adg.AndroidApp
import ru.weatherclock.adg.app.data.dto.AppSettings

internal actual object PlatformHelper {

    actual val fsHelper: FsHelper by lazy { FsHelper() }

    // Ключ вводится на экране настроек — в Android-сборки не вшивается
    actual val defaultWeatherApiKey: String
        get() = ""

    actual val appSettings: KStore<AppSettings> by lazy {
        storeOf(
            file = "${fsHelper.appStoragePath}/weather_settings.json".toPath(),
            default = AppSettings(),
            json = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                prettyPrint = true
            })
    }

    // Системное хранилище CA на планшете обновлено (13.11.2026) — бандл
    // корней в приложении удалён. История решения для старых ROM:
    // коммиты 0e6394a и последующие (OkHttp + траст «система → res/raw»)
    actual val defaultHttpClientEngine: HttpClientEngine by lazy {
        Android.create()
    }
    actual val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    actual val systemLocale: String by lazy {
//        val locale: Locale = Locale.getDefault()
//        val lang: String = locale.displayLanguage
//        val country: String = locale.displayCountry
//        return "${country}-${lang}".lowercase()
        ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)?.toLanguageTag()
            ?.replace(
                "_", "-"
            )?.lowercase() ?: "en-us"
    }

    actual fun createDbDriver(
        dbName: String, schema: SqlSchema<QueryResult.Value<Unit>>
    ): SqlDriver {
        return AndroidSqliteDriver(
            schema = schema,
            context = AndroidApp.INSTANCE,
            name = dbName,
            callback = object : AndroidSqliteDriver.Callback(schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            })
    }

    @Composable
    actual fun SystemAppearance(isDark: Boolean) {
        val view = LocalView.current
        val systemBarColor = Color.TRANSPARENT
        LaunchedEffect(isDark) {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            // Прозрачные системные панели нужны только до Android 15:
            // начиная с API 35 edge-to-edge включён принудительно,
            // системные панели всегда прозрачны, сеттеры — deprecated-нооп
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                @Suppress("DEPRECATION")
                window.statusBarColor = systemBarColor
                @Suppress("DEPRECATION")
                window.navigationBarColor = systemBarColor
            }
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = isDark
                isAppearanceLightNavigationBars = isDark
            }
        }
    }
}