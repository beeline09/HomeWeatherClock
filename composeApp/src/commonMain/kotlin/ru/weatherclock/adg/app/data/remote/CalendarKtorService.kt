package ru.weatherclock.adg.app.data.remote

import ru.weatherclock.adg.app.data.dto.ProductionCalendarDto

abstract class CalendarKtorService {

    abstract suspend fun getProductionCalendar(
        period: String,
        region: Int = 0
    ): ProductionCalendarDto
}
