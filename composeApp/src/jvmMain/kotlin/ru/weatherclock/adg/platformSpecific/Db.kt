package ru.weatherclock.adg.platformSpecific

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ru.weatherclock.adg.db.Database

actual fun createDriver(): SqlDriver {
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:home_weather.db")
    Database.Schema.create(driver)
    return driver
}
