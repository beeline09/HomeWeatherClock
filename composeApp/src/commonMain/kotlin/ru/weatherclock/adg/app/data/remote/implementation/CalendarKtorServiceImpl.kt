package ru.weatherclock.adg.app.data.remote.implementation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import ru.weatherclock.adg.app.data.dto.ProductionCalendarDto
import ru.weatherclock.adg.app.data.remote.CalendarKtorService
import ru.weatherclock.adg.app.data.remote.Endpoints
import ru.weatherclock.adg.app.data.remote.getUrl

class CalendarKtorServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
): CalendarKtorService() {

    override suspend fun getProductionCalendar(period: String): ProductionCalendarDto =
        httpClient
            .get(
                baseUrl + Endpoints.PROD_CALENDAR.getUrl(period)
            )
            .body()
}
