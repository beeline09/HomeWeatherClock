package ru.weatherclock.adg.app.data.remote.implementation

import io.ktor.client.call.body
import ru.weatherclock.adg.app.data.dto.productionCalendar.ProductionCalendarDto
import ru.weatherclock.adg.app.data.query.ProdCalendarQuery
import ru.weatherclock.adg.app.data.remote.AppHttpClient
import ru.weatherclock.adg.app.data.remote.CalendarKtorService

class CalendarKtorServiceImpl(private val httpClient: AppHttpClient): CalendarKtorService() {

    override suspend fun getProductionCalendar(
        year: Int,
        region: Int
    ): ProductionCalendarDto = httpClient.get(
        ProdCalendarQuery(
            region = region,
            year = year
        )
    ).body()
}
