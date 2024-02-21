package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.apache.Apache

actual val defaultHttpClientEngine: HttpClientEngine by lazy {
    Apache.create()
}

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

actual val separatorChar: String = System.getProperty("file.separator")

actual val systemLocale: String
    get() {
        val lang: String = System.getProperty("user.language")
//        val country: String = System.getProperty("user.country")
        return "${lang}-${lang}".lowercase()
    }