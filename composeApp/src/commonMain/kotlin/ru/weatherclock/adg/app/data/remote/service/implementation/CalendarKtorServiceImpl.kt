package ru.weatherclock.adg.app.data.remote.service.implementation

import io.ktor.client.call.body
import ru.weatherclock.adg.app.data.dto.productionCalendar.XmlCalendarDto
import ru.weatherclock.adg.app.data.remote.AppHttpClient
import ru.weatherclock.adg.app.data.remote.query.calendar.XmlCalendarQuery
import ru.weatherclock.adg.app.data.remote.service.CalendarKtorService

class CalendarKtorServiceImpl(private val httpClient: AppHttpClient): CalendarKtorService() {

    override suspend fun getProductionCalendar(year: Int): XmlCalendarDto = httpClient.get(
        XmlCalendarQuery(year = year)
    ).body()
}
