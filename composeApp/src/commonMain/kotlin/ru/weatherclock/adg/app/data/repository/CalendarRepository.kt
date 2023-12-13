package ru.weatherclock.adg.app.data.repository

import ru.weatherclock.adg.app.data.dto.ProductionCalendarDto

abstract class CalendarRepository {

    abstract suspend fun getProductionCalendar(
        period: String,
        region: Int = 0
    ): ProductionCalendarDto
}
