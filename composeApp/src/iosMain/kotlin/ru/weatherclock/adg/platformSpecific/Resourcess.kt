package ru.weatherclock.adg.platformSpecific

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager

@OptIn(
    ExperimentalResourceApi::class,
    ExperimentalForeignApi::class
)
actual suspend fun String.byteArrayFromResources(): ByteArray {

    NSBundle.allBundles().forEach {
        (it as NSBundle).also { nsBundle ->
            val file = nsBundle.pathForResource(
                name = this,
                ofType = null,
                inDirectory = "files"
            )
            if (!file.isNullOrBlank()) {
                return NSFileManager.defaultManager.contentsAtPath(file)?.let { data ->
                    data.bytes?.readBytes(data.length.toInt())
                } ?: resource(file).readBytes()
            }
        }
    }
    return runBlocking {
        resource("files/$this").readBytes()
    }
    /* return NSBundle.mainBundle.pathForResource(
         name = this,
         ofType = null,
         inDirectory = "files"
     )!!.encodeToByteArray()*/
//    return resource(MR.files.casiohour.path).readBytes()
}