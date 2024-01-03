package ru.weatherclock.adg.platformSpecific

import dev.icerock.moko.resources.FileResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

@ExperimentalResourceApi
fun FileResource.byteArrayFromResources(onSuccess: (ByteArray) -> Unit) =
    fileName().byteArrayFromResources(onSuccess)