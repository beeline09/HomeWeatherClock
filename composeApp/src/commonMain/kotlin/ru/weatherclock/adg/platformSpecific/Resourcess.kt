package ru.weatherclock.adg.platformSpecific

import dev.icerock.moko.resources.FileResource

expect fun String.byteArrayFromResources(onSuccess: (ByteArray) -> Unit)

expect fun FileResource.fileName(): String