package ru.weatherclock.adg.app.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.app.data.dto.asDomainModel
import ru.weatherclock.adg.app.data.repository.CalendarRepository
import ru.weatherclock.adg.app.data.repository.ProdCalendarDbRepository
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.model.calendar.asDbModel
import ru.weatherclock.adg.app.domain.model.calendar.asDomainModel
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now

class CalendarUseCase(
    private val repository: CalendarRepository,
    private val prodCalendarRepository: ProdCalendarDbRepository
) {

    fun getProdCalendarFlow() = prodCalendarRepository.getProductionDaysFlow()

    suspend fun getProdCalendar(): List<ProdCalendarDay> {
        return prodCalendarRepository.getProductionDays().map { it.asDomainModel() }
    }

    suspend fun insert(day: ProdCalendarDay) {
        prodCalendarRepository.insert(day.asDbModel())
    }

    suspend fun insert(days: List<ProdCalendarDay>) {
        prodCalendarRepository.insert(days = days.map { it.asDbModel() })
    }

    operator fun invoke(period: String): Flow<List<ProdCalendarDay>> = flow {
        val response = repository.getProductionCalendar(period).days.asDomainModel()
        emit(response)
    }

    suspend fun getProductionCalendarForPeriod(
        period: String,
        region: Int = 0
    ): List<ProdCalendarDay> =
        repository.getProductionCalendar(
            period,
            region
        ).days.asDomainModel()

    suspend fun getProdCalendarForThisYear(region: Int): List<ProdCalendarDay> {
        val prodCalendarDays = getProdCalendar()
        val year = LocalDateTime.now().year
        return if (prodCalendarDays.none { it.date.year == year }) {
            println("DatabaseProdCalendar is empty or old. Request from network...")
            getProductionCalendarForPeriod(
                "$year",
                region
            ).also { days ->
                println("NetworkProdCalendar downloaded. Days count: ${days.size}. Saving into DB...")
                insert(days)
            }
        } else prodCalendarDays
    }
}
