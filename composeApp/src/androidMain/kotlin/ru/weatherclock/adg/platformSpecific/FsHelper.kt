package ru.weatherclock.adg.platformSpecific

import ru.weatherclock.adg.AndroidApp
import java.io.File

internal actual class FsHelper {
    actual val appStoragePath: String
        get() = AndroidApp.INSTANCE.filesDir.path
    actual val separatorChar: String
        get() = File.separator
}