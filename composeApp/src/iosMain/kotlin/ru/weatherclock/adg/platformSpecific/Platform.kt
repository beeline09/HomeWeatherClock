package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

actual fun platformModule() = module { single { Darwin.create() } }

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.Default