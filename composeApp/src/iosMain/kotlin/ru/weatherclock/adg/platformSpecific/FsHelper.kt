package ru.weatherclock.adg.platformSpecific

import platform.Foundation.NSHomeDirectory

internal actual class FsHelper {
    actual val appStoragePath: String
        get() = NSHomeDirectory()
    actual val separatorChar: String
        get() = "/"
}