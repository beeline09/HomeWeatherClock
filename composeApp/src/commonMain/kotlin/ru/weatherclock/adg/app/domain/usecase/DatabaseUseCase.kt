package ru.weatherclock.adg.app.domain.usecase

import ru.weatherclock.adg.app.data.repository.DatabaseRepository
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.model.calendar.asDbModel
import ru.weatherclock.adg.app.domain.model.calendar.asDomainModel

class DatabaseUseCase(private val repository: DatabaseRepository) {

    fun getProdCalendarFlow() = repository.getProductionDaysFlow()

    suspend fun getProdCalendar(): List<ProdCalendarDay> {
        return repository.getProductionDays().map { it.asDomainModel() }
    }

    suspend fun insert(day: ProdCalendarDay) {
        repository.insert(day.asDbModel())
    }

    suspend fun insert(days: List<ProdCalendarDay>) {
        repository.insert(days = days.map { it.asDbModel() })
    }

}