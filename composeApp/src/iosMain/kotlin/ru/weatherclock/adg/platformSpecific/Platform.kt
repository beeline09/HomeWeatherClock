package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual val defaultHttpClientEngine: HttpClientEngine by lazy {
    Darwin.create()
}

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default

actual val separatorChar: String = "/"

actual val systemLocale: String
    get() = with(NSLocale.currentLocale) {
        "${languageCode}-${languageCode}".lowercase()
    }