package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import io.ktor.client.engine.apache.Apache
import org.koin.dsl.module

actual fun platformModule() = module { single { Apache.create() } }

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO

actual val separatorChar: String = System.getProperty("file.separator")