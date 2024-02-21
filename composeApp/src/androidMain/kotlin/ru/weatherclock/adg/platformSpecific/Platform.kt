package ru.weatherclock.adg.platformSpecific

import java.io.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android

actual val defaultHttpClientEngine: HttpClientEngine by lazy {
    Android.create()
}

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

actual val separatorChar: String = File.separator

actual val systemLocale: String
    get() {
        val l = ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)
//        val locale: Locale = Locale.getDefault()
//        val lang: String = locale.displayLanguage
//        val country: String = locale.displayCountry
//        return "${country}-${lang}".lowercase()
        return l
            ?.toLanguageTag()
            ?.replace(
                "_",
                "-"
            )
            ?.lowercase() ?: "en-us"
    }