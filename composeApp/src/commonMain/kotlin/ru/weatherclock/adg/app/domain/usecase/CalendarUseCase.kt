package ru.weatherclock.adg.app.domain.usecase

import kotlinx.datetime.LocalDate
import ru.weatherclock.adg.app.data.dto.productionCalendar.asDomainModel
import ru.weatherclock.adg.app.data.repository.calendar.CalendarRemoteRepository
import ru.weatherclock.adg.app.data.repository.db.prodCalendar.ProdCalendarDbRepository
import ru.weatherclock.adg.app.data.repository.settings.ProdCalendarSettingsRepository
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.model.calendar.asDbModel
import ru.weatherclock.adg.app.domain.model.calendar.asDomainModel
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now

class CalendarUseCase(
    private val calendarRemoteRepository: CalendarRemoteRepository,
    private val prodCalendarDbRepository: ProdCalendarDbRepository,
    private val prodCalendarSettingsRepository: ProdCalendarSettingsRepository,
) {

    suspend fun isProdCalendarEnabled(): Boolean {
        return prodCalendarSettingsRepository.isRussia()
    }

    private suspend fun getProdCalendarForCurrentYearDb(year: Int): List<ProdCalendarDay> {
        return prodCalendarDbRepository.getByRegionAndYear(
            region = FEDERAL_REGION,
            year = year
        ).map { it.asDomainModel() }
    }

    suspend fun insert(days: List<ProdCalendarDay>) {
        prodCalendarDbRepository.insert(days = days.map { it.asDbModel(region = FEDERAL_REGION) })
    }

    private suspend fun getProductionCalendarRemote(year: Int): List<ProdCalendarDay> {
        return calendarRemoteRepository.getProductionCalendar(year = year).asDomainModel()
    }

    /**
     * Федеральный производственный календарь РФ (без региональных праздников)
     * на текущий год. Сначала читается кеш из БД, при отсутствии — загрузка
     * с xmlcalendar.ru и сохранение в БД.
     */
    suspend fun getProdCalendar(): List<ProdCalendarDay> {
        val year = LocalDate.now().year
        val daysFromDb = getProdCalendarForCurrentYearDb(year = year)
        return daysFromDb.ifEmpty {
            println("DatabaseProdCalendar is empty or old. Request from network...")
            getProductionCalendarRemote(year = year).also { days ->
                println("NetworkProdCalendar downloaded. Days count: ${days.size}. Saving into DB...")
                insert(days = days)
            }
        }
    }

    companion object {
        // 0 — «Вся Россия» (федеральный календарь)
        const val FEDERAL_REGION = 0
    }
}
