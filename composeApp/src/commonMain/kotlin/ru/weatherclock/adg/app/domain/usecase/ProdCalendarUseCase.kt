package ru.weatherclock.adg.app.domain.usecase

import ru.weatherclock.adg.app.data.repository.ProdCalendarDbRepository
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.model.calendar.asDbModel
import ru.weatherclock.adg.app.domain.model.calendar.asDomainModel

class ProdCalendarUseCase(private val repository: ProdCalendarDbRepository) {

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