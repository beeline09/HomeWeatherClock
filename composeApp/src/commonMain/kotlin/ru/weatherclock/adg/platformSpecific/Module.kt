package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.Module

expect fun platformModule(): Module

expect val ioDispatcher: CoroutineDispatcher