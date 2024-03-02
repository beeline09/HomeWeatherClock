package ru.weatherclock.adg.platformSpecific

internal expect class FsHelper {
    val appStoragePath: String
    val separatorChar: String
}