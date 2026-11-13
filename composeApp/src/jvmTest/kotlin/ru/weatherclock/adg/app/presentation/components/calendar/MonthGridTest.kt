package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.isoDayNumber
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Инварианты сетки месяца (неделя Пн..Вс, пн=колонка 0).
 * Регресс: формула «% 7 - 1» давала смещение -1 для воскресенья — месяцы,
 * начинающиеся с вс, уезжали на колонку влево (кружки дней на соседях).
 */
class MonthGridTest {

    @Test
    fun `первое число стоит в колонке своего дня недели`() {
        for (year in 2024..2030) {
            for (month in 1..12) {
                val first = LocalDateTime(year, month, 1, 0, 0)
                val grid = first.getMonthGrid()
                val firstDayPos = grid.first().indexOfFirst { it != null }
                assertEquals(
                    first.dayOfWeek.isoDayNumber - 1,
                    firstDayPos,
                    "1-е ${first.month} $year (${first.dayOfWeek}) не в своей колонке"
                )
            }
        }
    }

    @Test
    fun `месяц начинающийся в воскресенье не смещён`() {
        val november2026 = LocalDateTime(2026, 11, 1, 0, 0) // воскресенье
        assertEquals(DayOfWeek.SUNDAY, november2026.dayOfWeek)
        val grid = november2026.getMonthGrid()
        // Вс — последняя колонка первой недели: 6 null-ов, затем «1»
        assertEquals(6, grid.first().indexOfFirst { it != null })
        assertEquals(1, grid.first().last())
    }

    @Test
    fun `сетка содержит все дни месяца ровно один раз`() {
        for (year in 2025..2027) {
            for (month in 1..12) {
                val anyDay = LocalDateTime(year, month, 15, 0, 0)
                val grid = anyDay.getMonthGrid()
                grid.forEach { assertEquals(7, it.size, "строка не по 7 ячеек") }
                val days = grid.flatten().filterNotNull()
                assertEquals(
                    (1..anyDay.getLastDayOfMonth()).toList(),
                    days,
                    "дни ${anyDay.month} $year потеряны/дублированы"
                )
            }
        }
    }

    @Test
    fun `нет пустых строк в конце сетки`() {
        val february2026 = LocalDateTime(2026, 2, 1, 0, 0) // начинается в вс
        val grid = february2026.getMonthGrid()
        grid.forEach { row ->
            assertEquals(true, row.any { it != null }, "полностью пустая строка в сетке")
        }
    }
}
