package ru.weatherclock.adg.app.data.repository.db.prodCalendar.implementation

import ru.weatherclock.adg.app.data.repository.db.prodCalendar.ProdCalendarDbRepository
import ru.weatherclock.adg.app.data.util.fromDbToLocalDate
import ru.weatherclock.adg.app.data.util.toDbFormat
import ru.weatherclock.adg.app.domain.model.calendar.DayType
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.db.ProdCalendar.ProdCalendar
import ru.weatherclock.adg.db.ProdCalendarDb

class ProdCalendarDbRepositoryImpl(private val database: ProdCalendarDb) : ProdCalendarDbRepository {

    // Мапперы домен<->БД живут в data-слое (раньше — в domain/model,
    // из-за чего домен знал схему БД)

    private fun DayType.toDbData(): Pair<Int, String> = when (this) {
        is DayType.AdditionalDayOff -> 6 to typeText
        is DayType.NationalHoliday -> 3 to typeText
        is DayType.PreHoliday -> 5 to typeText
        is DayType.RegionalHoliday -> 4 to typeText
        DayType.Weekend -> 2 to ""
        DayType.WorkingDay -> 1 to ""
    }

    private fun Int.toDayType(typeText: String) = when (this) {
        2 -> DayType.Weekend
        3 -> DayType.NationalHoliday(typeText)
        4 -> DayType.RegionalHoliday(typeText)
        5 -> DayType.PreHoliday(typeText)
        6 -> DayType.AdditionalDayOff(typeText)
        else -> DayType.WorkingDay
    }

    private fun ProdCalendarDay.asDbModel(region: Int): ProdCalendar {
        val typeData = type.toDbData()
        return ProdCalendar(
            date = date.toDbFormat(),
            type_id = typeData.first,
            type_text = typeData.second,
            note = note,
            week_day = weekDay,
            working_hours = workingHours,
            region = region
        )
    }

    private fun ProdCalendar.asDomainModel(): ProdCalendarDay = ProdCalendarDay(
        date = date.fromDbToLocalDate(),
        type = type_id.toDayType(type_text.orEmpty()),
        note = note.orEmpty(),
        region = region,
        weekDay = week_day.orEmpty(),
        workingHours = working_hours ?: 0
    )

    override suspend fun insert(days: List<ProdCalendarDay>, region: Int) {
        database.transaction {
            days.forEach { day ->
                database.prodCalendarQueries.insert(day.asDbModel(region))
            }
        }
    }

    override suspend fun getByRegionAndYear(
        region: Int,
        year: Int
    ): List<ProdCalendarDay> {
        return database.transactionWithResult {
            database.prodCalendarQueries.getByYearAndRegion(
                region = region,
                value_ = year.toString()
            ).executeAsList().map { it.asDomainModel() }
        }
    }
}
