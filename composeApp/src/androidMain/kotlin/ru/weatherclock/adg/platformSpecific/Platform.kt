package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import io.ktor.client.engine.android.Android
import org.koin.dsl.module
import ru.weatherclock.adg.AndroidApp

actual fun platformModule() = module { single { Android.create() } }

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

actual suspend fun String.byteArrayFromResources(): ByteArray {
    val resourceId = AndroidApp.INSTANCE.resources.getIdentifier(
        substringBefore("."),
        "raw",
        AndroidApp.INSTANCE.packageName
    )
    return AndroidApp.INSTANCE.resources.openRawResource(resourceId)
        .readBytes()
}