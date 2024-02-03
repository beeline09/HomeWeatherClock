package ru.weatherclock.adg.platformSpecific

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ru.weatherclock.adg.AndroidApp
import ru.weatherclock.adg.db.AccuweatherDb
import ru.weatherclock.adg.db.ProdCalendarDb

actual fun createProdCalendarDbDriver(): SqlDriver {
    return AndroidSqliteDriver(
        schema = ProdCalendarDb.Schema,
        context = AndroidApp.INSTANCE,
        name = "prod_calendar.db",
        callback = object: AndroidSqliteDriver.Callback(ProdCalendarDb.Schema) {
            override fun onOpen(db: SupportSQLiteDatabase) {
                db.setForeignKeyConstraintsEnabled(true)
            }
        }
    )
}

actual fun createAccuweatherDbDriver(): SqlDriver {
    return AndroidSqliteDriver(
        schema = AccuweatherDb.Schema,
        context = AndroidApp.INSTANCE,
        name = "accuweather.db",
        callback = object: AndroidSqliteDriver.Callback(AccuweatherDb.Schema) {
            override fun onOpen(db: SupportSQLiteDatabase) {
                db.setForeignKeyConstraintsEnabled(true)
            }
        }
    )
}
