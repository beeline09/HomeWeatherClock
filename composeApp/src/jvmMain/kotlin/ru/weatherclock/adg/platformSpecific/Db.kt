package ru.weatherclock.adg.platformSpecific

import java.io.File
import java.util.Properties
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual fun createDbDriver(
    dbName: String,
    schema: SqlSchema<QueryResult.Value<Unit>>
): SqlDriver {
    val dirPath = "${appStorage}${separatorChar}"
    val dir = File(dirPath)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return JdbcSqliteDriver(url = "jdbc:sqlite:${dirPath}$dbName",
        properties = Properties().apply {
            put(
                "foreign_keys",
                "true"
            )
        }).also {
        schema.create(it)
    }
}
