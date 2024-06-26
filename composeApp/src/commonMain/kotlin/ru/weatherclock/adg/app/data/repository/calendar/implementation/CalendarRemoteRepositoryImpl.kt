package ru.weatherclock.adg.app.data.repository.calendar.implementation

import ru.weatherclock.adg.app.data.dto.productionCalendar.ProductionCalendarDto
import ru.weatherclock.adg.app.data.remote.service.CalendarKtorService
import ru.weatherclock.adg.app.data.repository.calendar.CalendarRemoteRepository

class CalendarRemoteRepositoryImpl(private val ktorService: CalendarKtorService):
    CalendarRemoteRepository {

    override suspend fun getProductionCalendar(
        year: Int,
        region: Int
    ): ProductionCalendarDto = ktorService.getProductionCalendar(
        year,
        region
    )
}
