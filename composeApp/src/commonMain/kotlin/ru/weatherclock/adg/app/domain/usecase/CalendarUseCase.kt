package ru.weatherclock.adg.app.domain.usecase

import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.app.data.dto.asDomainModel
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

    private suspend fun getProdCalendarForCurrentYearDb(
        region: Int,
        year: Int
    ): List<ProdCalendarDay> {
        return prodCalendarDbRepository.getByRegionAndYear(
            region = region,
            year = year
        ).map { it.asDomainModel() }
    }

    suspend fun insert(
        day: ProdCalendarDay,
        region: Int
    ) {
        prodCalendarDbRepository.insert(day.asDbModel(region = region))
    }

    suspend fun insert(
        days: List<ProdCalendarDay>,
        region: Int
    ) {
        prodCalendarDbRepository.insert(days = days.map { it.asDbModel(region = region) })
    }

    suspend fun isProdCalendarEnabled(): Boolean {
        return prodCalendarSettingsRepository.isRussia()
    }

    private suspend fun getProductionCalendarForPeriodRemote(
        year: Int,
        region: Int
    ): List<ProdCalendarDay> = calendarRemoteRepository.getProductionCalendar(
        year = year,
        region = region
    ).days.asDomainModel()

    suspend fun getProdCalendar(): List<ProdCalendarDay> {
        return if (prodCalendarSettingsRepository.isRussia()) {
            val year = LocalDateTime.now().year
            val region = prodCalendarSettingsRepository.getRussianRegionNumber()
            val prodCalendarDays = getProdCalendarForCurrentYearDb(
                year = year,
                region = region
            )
            prodCalendarDays.ifEmpty {
                println("DatabaseProdCalendar is empty or old. Request from network...")
                getProductionCalendarForPeriodRemote(
                    year = year,
                    region
                ).also { days ->
                    println("NetworkProdCalendar downloaded. Days count: ${days.size}. Saving into DB...")
                    insert(
                        days = days,
                        region = region
                    )
                }
            }
        } else emptyList()
    }
}
