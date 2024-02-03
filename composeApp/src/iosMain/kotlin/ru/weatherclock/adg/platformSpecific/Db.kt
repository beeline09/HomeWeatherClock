package ru.weatherclock.adg.platformSpecific

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import ru.weatherclock.adg.db.AccuweatherDb
import ru.weatherclock.adg.db.ProdCalendarDb

actual fun createProdCalendarDbDriver(): SqlDriver {
    return NativeSqliteDriver(
        schema = ProdCalendarDb.Schema,
        name = "prod_calendar.db",
        onConfiguration = { config: DatabaseConfiguration ->
            config.copy(
                extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true)
            )
        }
    )
}

actual fun createAccuweatherDbDriver(): SqlDriver {
    return NativeSqliteDriver(
        schema = AccuweatherDb.Schema,
        name = "accuweather.db",
        onConfiguration = { config: DatabaseConfiguration ->
            config.copy(
                extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true)
            )
        }
    )
}