package ru.weatherclock.adg.app.data.repository.db.prodCalendar

import kotlinx.coroutines.flow.Flow
import ru.weatherclock.adg.db.ProdCalendar

interface ProdCalendarDbRepository {

    suspend fun getAllProductionDays(): List<ProdCalendar>
    fun getAllProductionDaysFlow(): Flow<List<ProdCalendar>>
    suspend fun insert(day: ProdCalendar)
    suspend fun insert(days: List<ProdCalendar>)
    suspend fun getByRegionAndYear(
        region: Int,
        year: Int
    ): List<ProdCalendar>

    fun getByRegionAndYearFlow(
        region: Int,
        year: Int
    ): Flow<List<ProdCalendar>>
}