package ru.weatherclock.adg.app.data.repository

import kotlinx.coroutines.flow.Flow
import ru.weatherclock.adg.db.ProdCalendar

abstract class ProdCalendarDbRepository {

    abstract suspend fun getProductionDays(): List<ProdCalendar>
    abstract fun getProductionDaysFlow(): Flow<List<ProdCalendar>>
    abstract suspend fun insert(day: ProdCalendar)
    abstract suspend fun insert(days: List<ProdCalendar>)
}