package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module
import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun platformModule() = module { single { Darwin.create() } }

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default

actual val separatorChar: String = "/"

actual val systemLocale: String
    get() = with(NSLocale.currentLocale) {
        "${countryCode}-${languageCode}".lowercase()
    }