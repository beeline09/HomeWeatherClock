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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import ru.weatherclock.adg.AndroidApp
import ru.weatherclock.adg.R
import ru.weatherclock.adg.app.data.dto.AppSettings
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

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

    /**
     * OkHttp-движок с трастом «система + бандленные корни ISRG».
     * Кастомные ROM (планшет Android 7.1) имеют устаревшее системное
     * хранилище CA и игнорируют network security config — без явного
     * траста xmlcalendar.ru (Let's Encrypt) падает Trust anchor not found.
     */
    actual val defaultHttpClientEngine: HttpClientEngine by lazy {
        io.ktor.client.engine.okhttp.OkHttp.create {
            config {
                val trustManager = isrgAwareTrustManager()
                val sslContext = SSLContext.getInstance("TLS").apply {
                    init(null, arrayOf(trustManager), SecureRandom())
                }
                sslSocketFactory(sslContext.socketFactory, trustManager)
            }
        }
    }

    private fun isrgAwareTrustManager(): X509TrustManager {
        val factory = CertificateFactory.getInstance("X.509")
        val isrgStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply { load(null) }
        listOf(R.raw.isrg_root_x1, R.raw.isrg_root_x2).forEach { res ->
            AndroidApp.INSTANCE.resources.openRawResource(res).use { input ->
                isrgStore.setCertificateEntry("isrg_$res", factory.generateCertificate(input))
            }
        }
        fun trustManager(ks: KeyStore?) = TrustManagerFactory
            .getInstance(TrustManagerFactory.getDefaultAlgorithm())
            .apply { init(ks) }
            .trustManagers
            .filterIsInstance<X509TrustManager>()
            .first()

        val system = trustManager(null)
        val isrg = trustManager(isrgStore)
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) =
                system.checkClientTrusted(chain, authType)

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                try {
                    system.checkServerTrusted(chain, authType)
                } catch (e: CertificateException) {
                    // Системного хранилища нет/устарело — пробуем корни ISRG
                    isrg.checkServerTrusted(chain, authType)
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> =
                system.acceptedIssuers + isrg.acceptedIssuers
        }
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