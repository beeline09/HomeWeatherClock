package ru.weatherclock.adg.platformSpecific

import java.io.File
import java.util.Properties
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ru.weatherclock.adg.db.Database

actual fun createDriver(): SqlDriver {
    val dirPath = "${appStorage}${separatorChar}"
    val dir = File(dirPath)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return JdbcSqliteDriver(
        url = "jdbc:sqlite:${dirPath}home_weather.db",
        properties = Properties().apply {
            put(
                "foreign_keys",
                "true"
            )
        }).also {
        Database.Schema.create(it)
    }
}
