package ru.weatherclock.adg.platformSpecific

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.posix.memcpy

/**
 *
 * Apply custom fonts
 */
/*private val cache: MutableMap<String, Font> = mutableMapOf()

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun font(
    name: String,
    res: String,
    weight: FontWeight,
    style: FontStyle
): Font {
    return cache.getOrPut(res) {
        val byteArray = runBlocking { resource("font/$res.ttf").readBytes() }
        androidx.compose.ui.text.platform.Font(
            res,
            byteArray,
            weight,
            style
        )
    }
}*/

private val cache: MutableMap<String, Font> = mutableMapOf()

@Composable
actual fun font(
    name: String,
    res: String,
    weight: FontWeight,
    style: FontStyle
): Font {
    return cache.getOrPut(res) {
        // Ideally we'd use the resource() API here, but it doesn't seem to be working
        // in multi-module projects
        androidx.compose.ui.text.platform.Font(
            identity = res,
            data = readBundleFile("$res.ttf"),
            weight = weight,
            style = style,
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun readBundleFile(path: String): ByteArray {
    val fileManager = NSFileManager.defaultManager()
    val composeResourcesPath = NSBundle.mainBundle.resourcePath + "/" + path
    val contentsAtPath: NSData? = fileManager.contentsAtPath(composeResourcesPath)
    if (contentsAtPath != null) {
        val byteArray = ByteArray(contentsAtPath.length.toInt())
        byteArray.usePinned {
            memcpy(
                it.addressOf(0),
                contentsAtPath.bytes,
                contentsAtPath.length
            )
        }
        return byteArray
    } else {
        error("File $path not found in Bundle")
    }
}