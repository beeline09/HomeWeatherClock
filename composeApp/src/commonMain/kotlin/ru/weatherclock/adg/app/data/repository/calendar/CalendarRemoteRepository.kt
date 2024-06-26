package ru.weatherclock.adg.app.data.repository.calendar

import ru.weatherclock.adg.app.data.dto.productionCalendar.ProductionCalendarDto

interface CalendarRemoteRepository {

    suspend fun getProductionCalendar(
        year: Int,
        region: Int = 0
    ): ProductionCalendarDto
}
