package ru.weatherclock.adg.platformSpecific

import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

@ExperimentalResourceApi
actual suspend fun String.byteArrayFromResources(): ByteArray {
    return resource("files/$this").readBytes()
}