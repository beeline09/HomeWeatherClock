package ru.weatherclock.adg.app.data.repository.calendar

import ru.weatherclock.adg.app.data.dto.productionCalendar.XmlCalendarDto

interface CalendarRemoteRepository {

    suspend fun getProductionCalendar(year: Int): XmlCalendarDto
}
