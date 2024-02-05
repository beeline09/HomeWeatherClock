package ru.weatherclock.adg.platformSpecific

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration

actual fun createDbDriver(
    dbName: String,
    schema: SqlSchema<QueryResult.Value<Unit>>
): SqlDriver {
    return NativeSqliteDriver(schema = schema,
        name = dbName,
        onConfiguration = { config: DatabaseConfiguration ->
            config.copy(
                extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true)
            )
        })
}