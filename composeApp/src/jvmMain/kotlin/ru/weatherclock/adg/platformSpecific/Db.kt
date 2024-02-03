package ru.weatherclock.adg.platformSpecific

import java.io.File
import java.util.Properties
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ru.weatherclock.adg.db.AccuweatherDb
import ru.weatherclock.adg.db.ProdCalendarDb

actual fun createProdCalendarDbDriver(): SqlDriver {
    val dirPath = "${appStorage}${separatorChar}"
    val dir = File(dirPath)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return JdbcSqliteDriver(url = "jdbc:sqlite:${dirPath}prod_calendar.db",
        properties = Properties().apply {
            put(
                "foreign_keys",
                "true"
            )
        }).also {
        ProdCalendarDb.Schema.create(it)
    }
}

actual fun createAccuweatherDbDriver(): SqlDriver {
    val dirPath = "${appStorage}${separatorChar}"
    val dir = File(dirPath)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return JdbcSqliteDriver(url = "jdbc:sqlite:${dirPath}accuweather.db",
        properties = Properties().apply {
            put(
                "foreign_keys",
                "true"
            )
        }).also {
        AccuweatherDb.Schema.create(it)
    }
}
