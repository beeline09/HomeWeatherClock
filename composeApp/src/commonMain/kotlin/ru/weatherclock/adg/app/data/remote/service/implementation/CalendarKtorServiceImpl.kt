package ru.weatherclock.adg.app.data.remote.service.implementation

import io.ktor.client.statement.bodyAsText
import nl.adaptivity.xmlutil.serialization.XML
import ru.weatherclock.adg.app.data.dto.productionCalendar.XmlCalendarDto
import ru.weatherclock.adg.app.data.remote.AppHttpClient
import ru.weatherclock.adg.app.data.remote.query.calendar.XmlCalendarQuery
import ru.weatherclock.adg.app.data.remote.service.CalendarKtorService

class CalendarKtorServiceImpl(private val httpClient: AppHttpClient): CalendarKtorService() {

    override suspend fun getProductionCalendar(year: Int): XmlCalendarDto {
        //Ответ xmlcalendar.ru приходит с Content-Type text/xml, который
        //ContentNegotiation не матчится с application/xml — поэтому тело
        //берём текстом и декодируем XML-форматом xmlutil напрямую
        val xml: String = httpClient.get(XmlCalendarQuery(year = year)).bodyAsText()
        return XML.decodeFromString<XmlCalendarDto>(xml)
    }
}
