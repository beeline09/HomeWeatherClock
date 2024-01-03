package ru.weatherclock.adg.platformSpecific

import dev.icerock.moko.resources.FileResource

actual fun FileResource.fileName(): String {
    return if (extension.isNotBlank()) {
        "${fileName}.${extension}"
    } else fileName
}