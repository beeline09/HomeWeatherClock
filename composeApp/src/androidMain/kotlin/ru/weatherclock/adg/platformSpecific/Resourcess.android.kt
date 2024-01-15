package ru.weatherclock.adg.platformSpecific

import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.runBlocking
import android.annotation.SuppressLint
import android.content.res.Resources
import dev.icerock.moko.resources.FileResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ru.weatherclock.adg.AndroidApp
import ru.weatherclock.adg.app.presentation.components.player.ResourceWrapper

actual fun FileResource.fileName(): String {
    val resources: Resources = AndroidApp.INSTANCE.resources
    return resources.getString(rawResId).substringAfterLast("/")
}

@OptIn(ExperimentalResourceApi::class)
@SuppressLint("DiscouragedApi")
actual fun String.rawResource(): ResourceWrapper {
    val resourceId = AndroidApp.INSTANCE.resources.getIdentifier(
        substringBefore("."),
        "raw",
        AndroidApp.INSTANCE.packageName
    )
    return runBlocking {
        val ba = AndroidApp.INSTANCE.resources.openRawResource(resourceId)
            .readBytes()
        val outputDir = File(
            AndroidApp.INSTANCE.filesDir,
            "raw"
        )
        outputDir.mkdirs()
        val outputFile = File(
            outputDir,
            this@rawResource
        )
        if (!outputFile.exists()) {
            outputFile.createNewFile()
            val fos = FileOutputStream(outputFile.path)
            fos.use {
                it.write(ba)
            }
        }
        ResourceWrapper(outputFile.path)
    }
}