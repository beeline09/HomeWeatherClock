package ru.weatherclock.adg.platformSpecific

import android.content.res.Resources
import dev.icerock.moko.resources.FileResource
import ru.weatherclock.adg.AndroidApp

actual fun FileResource.fileName(): String {
    val resources: Resources = AndroidApp.INSTANCE.resources
    return resources.getString(rawResId).substringAfterLast("/")
}