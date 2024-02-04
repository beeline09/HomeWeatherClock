package ru.weatherclock.adg.app.data.remote.service

import ru.weatherclock.adg.app.data.dto.productionCalendar.ProductionCalendarDto

abstract class CalendarKtorService {

    abstract suspend fun getProductionCalendar(
        year: Int,
        region: Int = 0
    ): ProductionCalendarDto
}
