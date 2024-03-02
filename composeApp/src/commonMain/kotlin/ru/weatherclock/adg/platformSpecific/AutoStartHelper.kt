package ru.weatherclock.adg.platformSpecific

expect object AutoStartHelper {

    val isSupported: Boolean
    val isEnabled: Boolean
    fun enable()
}