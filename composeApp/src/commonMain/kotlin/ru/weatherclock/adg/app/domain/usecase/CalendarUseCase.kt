package ru.weatherclock.adg.app.domain.usecase

import kotlinx.datetime.LocalDate
import ru.weatherclock.adg.app.data.dto.productionCalendar.asDomainModel
import ru.weatherclock.adg.app.data.repository.calendar.CalendarRemoteRepository
import ru.weatherclock.adg.app.data.repository.db.prodCalendar.ProdCalendarDbRepository
import ru.weatherclock.adg.app.data.repository.settings.ProdCalendarSettingsRepository
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.model.calendar.asDbModel
import ru.weatherclock.adg.app.domain.model.calendar.asDomainModel
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now

class CalendarUseCase(
    private val calendarRemoteRepository: CalendarRemoteRepository,
    private val prodCalendarDbRepository: ProdCalendarDbRepository,
    private val prodCalendarSettingsRepository: ProdCalendarSettingsRepository,
) {

    suspend fun isProdCalendarEnabled(): Boolean {
        return prodCalendarSettingsRepository.isRussia()
    }

    private suspend fun getProdCalendarForCurrentYearDb(year: Int): List<ProdCalendarDay> {
        return prodCalendarDbRepository.getByRegionAndYear(
            region = CALENDAR_DB_REGION,
            year = year
        ).map { it.asDomainModel() }
    }

    suspend fun insert(days: List<ProdCalendarDay>) {
        prodCalendarDbRepository.insert(days = days.map { it.asDbModel(region = CALENDAR_DB_REGION) })
    }

    private suspend fun getProductionCalendarRemote(year: Int): List<ProdCalendarDay> {
        return calendarRemoteRepository.getProductionCalendar(year = year).asDomainModel()
    }

    /**
     * Федеральный производственный календарь РФ (без региональных праздников)
     * на текущий год. Сначала читается кеш из БД, при отсутствии — загрузка
     * с xmlcalendar.ru и сохранение в БД.
     */
    suspend fun getProdCalendar(): List<ProdCalendarDay> {
        val year = LocalDate.now().year
        val daysFromDb = getProdCalendarForCurrentYearDb(year = year)
        return daysFromDb.ifEmpty {
            println("DatabaseProdCalendar is empty or old. Request from network...")
            //Ошибки сети/разбора календаря не критичны — тихий фолбэк на кеш
            //(без гигантских снекбаров от xmlutil-исключений)
            try {
                getProductionCalendarRemote(year = year).also { days ->
                    println("NetworkProdCalendar downloaded. Days count: ${days.size}. Saving into DB...")
                    insert(days = days)
                }
            } catch (e: Exception) {
                println("Calendar download failed: ${e.message}")
                e.printStackTrace()
                daysFromDb
            }
        }
    }

    companion object {
        /**
         * Маркер источника данных в колонке region БД.
         * 0 — старые данные из json без названий праздников,
         * 1 — данные calendar.xml с названиями праздников,
         * 2 — + подписи сокращённых дней,
         * 3 — note хранит сырые данные (дата переноса, название праздника),
         *     фразы собираются в UI из ресурсных шаблонов (локализация).
         * Смена значения инвалидирует кеш: старые строки перестают читаться.
         */
        const val CALENDAR_DB_REGION = 3
    }
}
