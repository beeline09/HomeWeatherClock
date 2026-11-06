package ru.weatherclock.adg.app.data.remote.service

import ru.weatherclock.adg.app.data.dto.productionCalendar.XmlCalendarDto

abstract class CalendarKtorService {

    abstract suspend fun getProductionCalendar(year: Int): XmlCalendarDto
}
