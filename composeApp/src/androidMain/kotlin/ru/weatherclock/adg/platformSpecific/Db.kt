package ru.weatherclock.adg.platformSpecific

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ru.weatherclock.adg.AndroidApp
import ru.weatherclock.adg.db.Database

actual fun createDriver(): SqlDriver {
    return AndroidSqliteDriver(
        Database.Schema,
        AndroidApp.INSTANCE,
        "weather_clock.db"
    )
}
