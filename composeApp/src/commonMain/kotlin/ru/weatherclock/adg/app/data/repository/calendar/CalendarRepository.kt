package ru.weatherclock.adg.app.data.repository.calendar

import ru.weatherclock.adg.app.data.dto.ProductionCalendarDto

interface CalendarRepository {

    suspend fun getProductionCalendar(
        period: String,
        region: Int = 0
    ): ProductionCalendarDto
}
