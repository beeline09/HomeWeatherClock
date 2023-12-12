package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import ru.weatherclock.adg.db.Database

expect fun createDriver(): SqlDriver

fun createDatabase(): Database {
    return Database(createDriver())
}

expect fun platformModule(): Module

expect val ioDispatcher: CoroutineDispatcher