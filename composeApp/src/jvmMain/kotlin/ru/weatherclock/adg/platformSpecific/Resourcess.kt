package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import ru.weatherclock.adg.app.presentation.components.player.ResourceWrapper

@ExperimentalResourceApi
actual fun String.byteArrayFromResources(onSuccess: (ByteArray) -> Unit) {
    runBlocking {
        resource("files/${this@byteArrayFromResources}")
        val result = resource("files/${this@byteArrayFromResources}").readBytes()
        onSuccess(result)
    }
}

@OptIn(ExperimentalResourceApi::class)
actual fun String.rawResource(): ResourceWrapper {
    return ResourceWrapper("files/${this@rawResource}")
}