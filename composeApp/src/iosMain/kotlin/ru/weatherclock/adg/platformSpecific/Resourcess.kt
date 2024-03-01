package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.runBlocking
import platform.Foundation.NSBundle
import ru.weatherclock.adg.app.presentation.components.player.ResourceWrapper

actual fun String.rawResource(): ResourceWrapper {
    var path: String? = null
    NSBundle.allBundles().forEach {
        (it as NSBundle).also { nsBundle ->
            val file = nsBundle.pathForResource(
                name = this, ofType = null, inDirectory = "files"
            )
            if (!file.isNullOrBlank()) {
                path = file
            }
        }
    }
    return runBlocking {
        ResourceWrapper(path ?: error("File does not exist!"))
    }
}