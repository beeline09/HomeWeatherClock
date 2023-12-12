package ru.weatherclock.adg.platformSpecific

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import ru.weatherclock.adg.db.Database

actual fun createDriver(): SqlDriver {
    return NativeSqliteDriver(
        Database.Schema,
        "weather_clock.db"
    )
}