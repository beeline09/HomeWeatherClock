package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

@ExperimentalResourceApi
actual fun String.byteArrayFromResources(onSuccess: (ByteArray) -> Unit) {
    runBlocking {
        val result = resource("files/${this@byteArrayFromResources}").readBytes()
        onSuccess(result)
    }
}