package ru.weatherclock.adg.app.data.repository.db.prodCalendar

import kotlinx.coroutines.flow.Flow
import ru.weatherclock.adg.db.ProdCalendar

interface ProdCalendarDbRepository {

    suspend fun getProductionDays(): List<ProdCalendar>
    fun getProductionDaysFlow(): Flow<List<ProdCalendar>>
    suspend fun insert(day: ProdCalendar)
    suspend fun insert(days: List<ProdCalendar>)
}