package ru.weatherclock.adg.app.data.repository.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import ru.weatherclock.adg.app.data.repository.ProdCalendarDbRepository
import ru.weatherclock.adg.db.Database
import ru.weatherclock.adg.db.ProdCalendar

class ProdCalendarDbRepositoryImpl(private val database: Database): ProdCalendarDbRepository() {

    override suspend fun getProductionDays(): List<ProdCalendar> {
        return database.prodCalendarQueries.selectAll().executeAsList()
    }

    override fun getProductionDaysFlow(): Flow<List<ProdCalendar>> {
        return database.prodCalendarQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun insert(day: ProdCalendar) {
        database.transaction {
            database.prodCalendarQueries.insert(day)
        }
    }

    override suspend fun insert(days: List<ProdCalendar>) {
        database.transaction {
            days.forEach { day ->
                database.prodCalendarQueries.insert(day)
            }
        }
    }
}