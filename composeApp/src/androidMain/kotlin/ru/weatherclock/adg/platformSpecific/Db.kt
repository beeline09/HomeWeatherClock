package ru.weatherclock.adg.platformSpecific

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ru.weatherclock.adg.AndroidApp

actual fun createDbDriver(
    dbName: String,
    schema: SqlSchema<QueryResult.Value<Unit>>
): SqlDriver {
    return AndroidSqliteDriver(schema = schema,
        context = AndroidApp.INSTANCE,
        name = dbName,
        callback = object: AndroidSqliteDriver.Callback(schema) {
            override fun onOpen(db: SupportSQLiteDatabase) {
                db.setForeignKeyConstraintsEnabled(true)
            }
        })
}
