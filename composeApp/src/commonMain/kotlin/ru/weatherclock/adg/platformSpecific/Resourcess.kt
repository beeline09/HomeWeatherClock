package ru.weatherclock.adg.platformSpecific

import dev.icerock.moko.resources.FileResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ru.weatherclock.adg.app.presentation.components.player.ResourceWrapper

expect fun String.byteArrayFromResources(onSuccess: (ByteArray) -> Unit)

@OptIn(ExperimentalResourceApi::class)
expect fun String.rawResource(): ResourceWrapper

expect fun FileResource.fileName(): String