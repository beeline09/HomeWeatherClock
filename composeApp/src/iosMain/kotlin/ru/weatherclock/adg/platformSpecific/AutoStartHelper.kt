package ru.weatherclock.adg.platformSpecific

actual object AutoStartHelper {

    actual val isSupported: Boolean = false
    actual val isEnabled: Boolean = false

    actual fun enable() {
    }

}