package ru.weatherclock.adg.app.data.repository.db.prodCalendar

import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay

/** Интерфейс на ДОМЕННЫХ типах: слой domain не знает про схему БД */
interface ProdCalendarDbRepository {

    suspend fun insert(days: List<ProdCalendarDay>, region: Int)

    suspend fun getByRegionAndYear(
        region: Int,
        year: Int
    ): List<ProdCalendarDay>
}
