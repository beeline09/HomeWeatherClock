package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import io.ktor.client.engine.java.*

actual fun platformModule() = module { single { Java.create() } }

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO