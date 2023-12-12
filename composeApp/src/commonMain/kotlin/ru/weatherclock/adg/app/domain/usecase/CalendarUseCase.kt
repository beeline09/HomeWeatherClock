package ru.weatherclock.adg.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.weatherclock.adg.app.data.dto.asDomainModel
import ru.weatherclock.adg.app.data.repository.CalendarRepository
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay

class CalendarUseCase(private val repository: CalendarRepository) {

    operator fun invoke(period: String): Flow<List<ProdCalendarDay>> = flow {
        val response = repository.getProductionCalendar(period).days.asDomainModel()
        emit(response)
    }

    suspend fun getForPeriod(period: String): List<ProdCalendarDay> =
        repository.getProductionCalendar(period).days.asDomainModel()
}
