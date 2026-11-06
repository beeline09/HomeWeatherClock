package ru.weatherclock.adg.app.data.remote.query.calendar

import ru.weatherclock.adg.app.data.remote.query.ApiQuery

/**
 * Федеральный производственный календарь РФ (xmlcalendar.ru, бесплатно, без ключа).
 * Запрашивается именно calendar.xml (не .json): только XML-формат несёт
 * легенду с названиями праздников, которые показываются при тапе на день.
 */
class XmlCalendarQuery(
    private val year: Int
) : ApiQuery {

    override fun buildUrl(): String = buildString {
        append("https://xmlcalendar.ru/data/ru/")
        append(year)
        append("/calendar.xml")
    }
}
