package ru.weatherclock.adg.app.data.repository.implementation

import ru.weatherclock.adg.app.data.dto.ProductionCalendarDto
import ru.weatherclock.adg.app.data.remote.CalendarKtorService
import ru.weatherclock.adg.app.data.repository.CalendarRepository

class CalendarRepositoryImpl(private val ktorService: CalendarKtorService): CalendarRepository() {

    override suspend fun getProductionCalendar(
        period: String,
        region: Int
    ): ProductionCalendarDto =
        ktorService.getProductionCalendar(
            period,
            region
        )
}
