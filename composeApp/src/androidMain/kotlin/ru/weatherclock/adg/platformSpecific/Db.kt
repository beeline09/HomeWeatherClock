package ru.weatherclock.adg.platformSpecific

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ru.weatherclock.adg.AndroidApp
import ru.weatherclock.adg.db.Database

actual fun createDriver(): SqlDriver {
    return AndroidSqliteDriver(
        schema = Database.Schema,
        context = AndroidApp.INSTANCE,
        name = "weather_clock.db",
        callback = object: AndroidSqliteDriver.Callback(Database.Schema) {
            override fun onOpen(db: SupportSQLiteDatabase) {
                db.setForeignKeyConstraintsEnabled(true)
            }
        }
    )
}
