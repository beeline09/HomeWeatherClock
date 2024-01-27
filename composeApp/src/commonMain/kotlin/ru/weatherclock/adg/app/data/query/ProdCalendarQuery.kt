package ru.weatherclock.adg.app.data.query

import ru.weatherclock.adg.app.data.query.base.ApiQuery
import ru.weatherclock.adg.app.presentation.components.util.padStart

class ProdCalendarQuery(
    private val region: Int,
    private val year: Int
): ApiQuery {

    override fun buildUrl(): String = buildString {
        append("https://production-calendar.ru/get/ru/")
        append(year.padStart(4))
        append("/json?")
        if (region > 0) {
            append("region=")
            append(region)
            append("&")
        }
        append("compact=true")
    }

}