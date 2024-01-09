package ru.weatherclock.adg.platformSpecific

import kotlinx.coroutines.CoroutineDispatcher
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import ru.weatherclock.adg.db.Database
import ru.weatherclock.adg.db.ProdCalendar

expect fun createDriver(): SqlDriver

private val intAdapter = object: ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()

    override fun encode(value: Int): Long = value.toLong()
}

fun createDatabase(): Database {
    return Database(
        driver = createDriver(),
        ProdCalendarAdapter = ProdCalendar.Adapter(
            day_of_monthAdapter = intAdapter,
            monthAdapter = intAdapter,
            yearAdapter = intAdapter
        )
    )
}

expect fun platformModule(): Module

expect val ioDispatcher: CoroutineDispatcher