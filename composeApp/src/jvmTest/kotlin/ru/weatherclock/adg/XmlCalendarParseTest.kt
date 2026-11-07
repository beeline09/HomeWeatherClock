package ru.weatherclock.adg

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.Test
import ru.weatherclock.adg.app.data.dto.productionCalendar.XmlCalendarDto
import ru.weatherclock.adg.app.data.dto.productionCalendar.asDomainModel
import ru.weatherclock.adg.app.domain.model.calendar.DayType
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Десериализация calendar.xml (xmlcalendar.ru, ktor ContentNegotiation XML /
 * xmlutil) и маппинг в домен: названия праздников, переносы, сокращённые дни,
 * будни/выходные.
 */
class XmlCalendarParseTest {

    private val xml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <calendar year="2026" lang="ru" date="2025.09.30" country="ru">
            <holidays>
                <holiday id="1" title="Новогодние каникулы"/>
                <holiday id="7" title="День России"/>
            </holidays>
            <days>
                <day d="01.01" t="1" h="1"/>
                <day d="01.09" t="1" f="01.03"/>
                <day d="04.30" t="2"/>
                <day d="06.12" t="1" h="7"/>
            </days>
        </calendar>
    """.trimIndent()

    private val dto = XML.decodeFromString<XmlCalendarDto>(xml)

    private val days = dto.asDomainModel()

    private fun day(month: Int, day: Int) =
        days.first { it.date == LocalDate(2026, month, day) }

    @Test
    fun `dto fields deserialized`() {
        assertEquals(2026, dto.year)
        assertEquals("Новогодние каникулы", dto.holidays.holiday.first { it.id == 1 }.title)
        assertEquals(4, dto.days.day.size)
    }

    @Test
    fun `holiday day carries title`() {
        val d = day(1, 1)
        val type = assertIs<DayType.NationalHoliday>(d.type)
        assertEquals("Новогодние каникулы", type.typeText)
    }

    @Test
    fun `transferred day off stores raw transfer date in note`() {
        //note — сырые данные (ММ.ДД), фраза собирается в UI из ресурсного шаблона
        val d = day(1, 9)
        assertIs<DayType.AdditionalDayOff>(d.type)
        assertEquals("01.03", d.note)
    }

    @Test
    fun `shortened day is preholiday with 7 working hours`() {
        val d = day(4, 30)
        val type = assertIs<DayType.PreHoliday>(d.type)
        assertEquals(7, d.workingHours)
    }

    @Test
    fun `shortened day stores next holiday title in note`() {
        //note — сырые данные: название ближайшего праздника после сокращённого дня
        //(в фикстуре после 30.04 — 12 июня, День России)
        val d = day(4, 30)
        assertIs<DayType.PreHoliday>(d.type)
        assertEquals("День России", d.note)
    }

    @Test
    fun `regular tuesday is working day`() {
        // 13 января 2026 — вторник, в списке особых дней отсутствует
        assertEquals(DayOfWeek.TUESDAY, LocalDate(2026, 1, 13).dayOfWeek)
        assertIs<DayType.WorkingDay>(day(1, 13).type)
    }

    @Test
    fun `regular saturday is weekend`() {
        assertEquals(DayOfWeek.SATURDAY, LocalDate(2026, 1, 17).dayOfWeek)
        assertIs<DayType.Weekend>(day(1, 17).type)
    }

    @Test
    fun `full year generated`() {
        assertEquals(365, days.size)
    }
}
