package ru.weatherclock.adg.platformSpecific

import HomeWeatherClock.composeApp.BuildConfig
import net.harawata.appdirs.AppDirsFactory
import java.nio.file.FileSystems

internal actual class FsHelper {
    actual val appStoragePath: String
        get() = AppDirsFactory.getInstance().getUserDataDir(
            BuildConfig.APP_NAME,
            BuildConfig.APP_PACKAGE_NAME,
            BuildConfig.APP_AUTHOR
        )
    actual val separatorChar: String
        get() = FileSystems.getDefault().separator
}