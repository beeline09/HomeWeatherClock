package ru.weatherclock.adg.platformSpecific

import kotlinx.cinterop.ExperimentalForeignApi
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import okio.Path.Companion.toPath
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import ru.weatherclock.adg.app.domain.model.settings.AppSettings

actual val appSettingsKStore: KStore<AppSettings> by lazy {
    storeOf(
        file = "${iosDirPath(folder = "prefs")}${separatorChar}weather_settings.json".toPath(),
        default = AppSettings()
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun iosDirPath(folder: String): String {
    val paths = NSSearchPathForDirectoriesInDomains(
        NSApplicationSupportDirectory,
        NSUserDomainMask,
        true
    );
    val documentsDirectory = paths[0] as String;

    val databaseDirectory = "$documentsDirectory/$folder"

    val fileManager = NSFileManager.defaultManager()

    if (!fileManager.fileExistsAtPath(databaseDirectory)) fileManager.createDirectoryAtPath(
        databaseDirectory,
        true,
        null,
        null
    ); //Create folder

    return databaseDirectory
}