package ru.weatherclock.adg.platformSpecific

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import kotlinx.coroutines.runBlocking
import dev.icerock.moko.resources.FileResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import ru.weatherclock.adg.app.presentation.components.player.ResourceWrapper

@OptIn(
    ExperimentalResourceApi::class,
    ExperimentalForeignApi::class
)
actual fun String.byteArrayFromResources(onSuccess: (ByteArray) -> Unit) {

    NSBundle.allBundles().forEach {
        (it as NSBundle).also { nsBundle ->
            val file = nsBundle.pathForResource(
                name = this,
                ofType = null,
                inDirectory = "files"
            )
            if (!file.isNullOrBlank()) {
                runBlocking {
                    val result = NSFileManager.defaultManager.contentsAtPath(file)?.let { data ->
                        data.bytes?.readBytes(data.length.toInt())
                    } ?: resource(file).readBytes()
                    onSuccess(result)
                }
            }
        }
    }
    return runBlocking {
        resource("files/$this").readBytes()
    }
}

actual fun FileResource.fileName(): String {
    return if (extension.isNotBlank()) {
        "${fileName}.${extension}"
    } else fileName
}

@OptIn(ExperimentalResourceApi::class)
actual fun String.rawResource(): ResourceWrapper {
    var path: String? = null
    NSBundle.allBundles().forEach {
        (it as NSBundle).also { nsBundle ->
            val file = nsBundle.pathForResource(
                name = this,
                ofType = null,
                inDirectory = "files"
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