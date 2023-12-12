package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import io.ktor.client.engine.android.Android
import org.koin.dsl.module

actual fun platformModule() = module { single { Android.create() } }

actual val ioDispatcher: CoroutineDispatcher
    get() = Dispatchers.IO