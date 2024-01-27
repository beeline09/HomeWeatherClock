package ru.weatherclock.adg.app.data.repository.db.prodCalendar.implementation

import kotlinx.coroutines.flow.Flow
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import ru.weatherclock.adg.app.data.repository.db.prodCalendar.ProdCalendarDbRepository
import ru.weatherclock.adg.db.Database
import ru.weatherclock.adg.db.ProdCalendar
import ru.weatherclock.adg.platformSpecific.ioDispatcher

class ProdCalendarDbRepositoryImpl(private val database: Database): ProdCalendarDbRepository {

    override suspend fun getAllProductionDays(): List<ProdCalendar> {
        return database.prodCalendarQueries.selectAll().executeAsList()
    }

    override fun getAllProductionDaysFlow(): Flow<List<ProdCalendar>> {
        return database.prodCalendarQueries.selectAll().asFlow().mapToList(ioDispatcher)
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

    override suspend fun getByRegionAndYear(
        region: Int,
        year: Int
    ): List<ProdCalendar> {
        return database.transactionWithResult {
            database.prodCalendarQueries.getByYearAndRegion(
                region = region,
                value_ = year.toString()
            ).executeAsList()
        }
    }

    override fun getByRegionAndYearFlow(
        region: Int,
        year: Int
    ): Flow<List<ProdCalendar>> {
        return database.prodCalendarQueries.getByYearAndRegion(
            region = region,
            value_ = year.toString()
        ).asFlow().mapToList(ioDispatcher)
    }
}