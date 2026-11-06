package ru.weatherclock.adg.app.data.remote.query.calendar

import ru.weatherclock.adg.app.data.remote.query.ApiQuery

/**
 * Федеральный производственный календарь РФ (xmlcalendar.ru, бесплатно, без ключа).
 * Пример: https://xmlcalendar.ru/data/ru/2026/calendar.json
 */
class XmlCalendarQuery(
    private val year: Int
) : ApiQuery {

    override fun buildUrl(): String = buildString {
        append("https://xmlcalendar.ru/data/ru/")
        append(year)
        append("/calendar.json")
    }
}
