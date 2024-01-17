package ru.weatherclock.adg.platformSpecific

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import ru.weatherclock.adg.db.Database

actual fun createDriver(): SqlDriver {
    return NativeSqliteDriver(
        schema = Database.Schema,
        name = "weather_clock.db",
        onConfiguration = { config: DatabaseConfiguration ->
            config.copy(
                extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true)
            )
        }
    )
}