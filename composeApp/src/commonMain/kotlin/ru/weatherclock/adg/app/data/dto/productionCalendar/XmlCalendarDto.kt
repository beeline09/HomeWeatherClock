package ru.weatherclock.adg.app.data.dto.productionCalendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlOtherAttributes
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import ru.weatherclock.adg.app.domain.model.calendar.DayType
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay

/**
 * Федеральный производственный календарь РФ с xmlcalendar.ru.
 *
 * Источник — calendar.xml (НЕ calendar.json): только XML-формат несёт
 * легенду с НАЗВАНИЯМИ праздников, которые показываются при тапе на день.
 * Разбор — через ktor ContentNegotiation + kotlinx-xml (xmlutil):
 * примитивные свойства по умолчанию сериализуются как XML-АТРИБУТЫ.
 *
 * Формат дат в атрибутах d/f — ММ.ДД (месяц.день):
 * <day d="01.09" t="1" f="01.03"/> — 9 января, выходной перенесён с 3 января.
 * t="1" — праздничный/перенесённый нерабочий день, t="2" — сокращённый.
 * Региональных праздников сервис не содержит (только федеральные).
 */
@Serializable
@XmlSerialName("calendar")
data class XmlCalendarDto(
    @XmlSerialName("year") val year: Int = 0,
    @XmlElement(true) val holidays: XmlHolidays = XmlHolidays(),
    @XmlElement(true) val days: XmlDays = XmlDays(),
    //Необъявленные атрибуты корня (lang/date/country) собираются сюда
    //(ключи — "имя атрибута"), а не приводят к ошибке разбора
    @XmlOtherAttributes
    val otherAttributes: Map<String, String> = emptyMap(),
)

@Serializable
@XmlSerialName("holidays")
data class XmlHolidays(
    @XmlElement(true) val holiday: List<XmlHoliday> = emptyList()
)

@Serializable
@XmlSerialName("holiday")
data class XmlHoliday(
    @XmlSerialName("id") val id: Int = 0,
    @XmlSerialName("title") val title: String = "",
)

@Serializable
@XmlSerialName("days")
data class XmlDays(
    @XmlElement(true) val day: List<XmlDay> = emptyList()
)

@Serializable
@XmlSerialName("day")
data class XmlDay(
    /** ММ.ДД */
    @XmlSerialName("d") val d: String = "",
    /** 1 — праздничный/перенесённый нерабочий, 2 — сокращённый предпраздничный */
    @XmlSerialName("t") val t: Int = 1,
    /** ссылка на легенду праздника */
    @XmlSerialName("h") val h: Int? = null,
    /** с какой даты перенесён выходной (ММ.ДД) */
    @XmlSerialName("f") val f: String? = null,
) {
    /** d="04.30" + год -> LocalDate(год, 4, 30) */
    fun toLocalDate(year: Int): LocalDate? {
        val (month, day) = parseMonthDay(d) ?: return null
        return runCatching { LocalDate(year, month, day) }.getOrNull()
    }

    /** f="01.03" + год -> LocalDate(год, 1, 3) */
    fun transferredFrom(year: Int): LocalDate? {
        val (month, day) = parseMonthDay(f.orEmpty()) ?: return null
        return runCatching { LocalDate(year, month, day) }.getOrNull()
    }

    private fun parseMonthDay(value: String): Pair<Int, Int>? {
        val parts = value.split(".")
        val month = parts.getOrNull(0)?.toIntOrNull() ?: return null
        val day = parts.getOrNull(1)?.toIntOrNull() ?: return null
        return month to day
    }
}

private fun daysInMonth(year: Int, month: Int): Int {
    val firstDay = LocalDate(year, month, 1)
    return firstDay.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day
}

private fun LocalDate.isDayOffByWeek(): Boolean =
    dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY

fun XmlCalendarDto.asDomainModel(): List<ProdCalendarDay> {
    if (year <= 0) return emptyList()
    val titles = holidays.holiday.associate { it.id to it.title }
    val special = days.day.mapNotNull { dayDto ->
        dayDto.toLocalDate(year)?.let { it to dayDto }
    }.toMap()
    return (1..12).flatMap { month ->
        val lastDay = daysInMonth(year, month)
        (1..lastDay).map { day ->
            val date = LocalDate(year, month, day)
            val dayDto = special[date]
            val type: DayType = when {
                dayDto == null && date.isDayOffByWeek() -> DayType.Weekend
                dayDto == null -> DayType.WorkingDay
                dayDto.t == 2 -> DayType.PreHoliday(typeText = "")
                dayDto.h != null -> DayType.NationalHoliday(
                    typeText = titles[dayDto.h] ?: ""
                )

                else -> DayType.AdditionalDayOff(typeText = "")
            }
            val note = dayDto?.f
                ?.let {
                    val parts = it.split(".")
                    val dd = parts.getOrNull(1)?.padStart(2, '0') ?: it
                    val mm = parts.getOrNull(0)?.padStart(2, '0') ?: ""
                    "перенесён с $dd.$mm"
                }
                ?: ""
            ProdCalendarDay(
                date = date,
                type = type,
                note = note,
                weekDay = "",
                workingHours = when (type) {
                    is DayType.PreHoliday -> 7
                    DayType.WorkingDay -> 8
                    else -> 0
                },
                region = 0
            )
        }
    }
}
