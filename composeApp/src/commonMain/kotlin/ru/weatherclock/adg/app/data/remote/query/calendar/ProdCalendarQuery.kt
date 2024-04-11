package ru.weatherclock.adg.app.data.remote.query.calendar

import ru.weatherclock.adg.app.data.remote.query.ApiQuery
import ru.weatherclock.adg.app.presentation.components.util.padStart

class ProdCalendarQuery(
    private val region: Int,
    private val year: Int
): ApiQuery {

    override fun buildUrl(): String = buildString {
        append("https://production-calendar.ru/get-period/cf5ad9715c9687010bc5e1244c8c4e72/ru/")
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