package ru.weatherclock.adg.platformSpecific

import java.io.File
import java.util.Locale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import android.annotation.SuppressLint
import io.ktor.client.engine.android.Android
import org.koin.dsl.module
import ru.weatherclock.adg.AndroidApp

actual fun platformModule() = module { single { Android.create() } }

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

@SuppressLint("DiscouragedApi")
actual fun String.byteArrayFromResources(onSuccess: (ByteArray) -> Unit) {
    val resourceId = AndroidApp.INSTANCE.resources.getIdentifier(
        substringBefore("."),
        "raw",
        AndroidApp.INSTANCE.packageName
    )
    onSuccess(
        AndroidApp.INSTANCE.resources.openRawResource(resourceId)
            .readBytes()
    )
}

actual val separatorChar: String = File.separator

actual val systemLocale: String
    get() {
        val locale: Locale = Locale.getDefault()
        val lang: String = locale.displayLanguage
        val country: String = locale.displayCountry
        return "${country}-${lang}".lowercase()
    }