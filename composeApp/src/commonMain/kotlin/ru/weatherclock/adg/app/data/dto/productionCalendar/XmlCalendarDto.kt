package ru.weatherclock.adg.app.data.dto.productionCalendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.calendar.DayType
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay

/**
 * Ответ https://xmlcalendar.ru/data/ru/{год}/calendar.json — федеральный
 * производственный календарь РФ (региональные праздники не учитываются).
 *
 * В [XmlCalendarMonthDto.days] через запятую перечислены особые дни месяца
 * (легенда сверена с calendar.xml того же сервиса, где типы заданы явно):
 *  - число без пометки — нерабочий день (выходной или праздничный);
 *  - "N+" — дополнительный выходной по переносу (например "9+" — 9 января);
 *  - "N*" — сокращённый предпраздничный рабочий день (например "3*" — 3 ноября).
 */
@Serializable
data class XmlCalendarDto(
    @SerialName("year") val year: Int = 0,
    @SerialName("months") val months: List<XmlCalendarMonthDto> = emptyList()
)

@Serializable
data class XmlCalendarMonthDto(
    @SerialName("month") val month: Int = 0,
    @SerialName("days") val days: String = ""
)

private data class MonthDayNumbers(
    val nonWorking: Set<Int>,
    val transferredDaysOff: Set<Int>,
    val preHoliday: Set<Int>
)

private fun XmlCalendarMonthDto.parseDays(): MonthDayNumbers {
    val nonWorking = mutableSetOf<Int>()
    val transferredDaysOff = mutableSetOf<Int>()
    val preHoliday = mutableSetOf<Int>()
    days.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .forEach { token ->
            when {
                token.endsWith("+") -> token.removeSuffix("+").toIntOrNull()
                    ?.let { transferredDaysOff += it }

                token.endsWith("*") -> token.removeSuffix("*").toIntOrNull()
                    ?.let { preHoliday += it }

                else -> token.toIntOrNull()?.let { nonWorking += it }
            }
        }
    return MonthDayNumbers(
        nonWorking = nonWorking,
        transferredDaysOff = transferredDaysOff,
        preHoliday = preHoliday
    )
}

private fun daysInMonth(year: Int, month: Int): Int {
    val firstDay = LocalDate(year, month, 1)
    return firstDay.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day
}

private fun LocalDate.isDayOffByWeek(): Boolean =
    dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY

fun XmlCalendarDto.asDomainModel(): List<ProdCalendarDay> {
    if (year <= 0) return emptyList()
    return months.flatMap { monthDto ->
        if (monthDto.month !in 1..12) return@flatMap emptyList()
        val parsed = monthDto.parseDays()
        val lastDay = daysInMonth(year, monthDto.month)
        (1..lastDay).map { day ->
            val date = LocalDate(year, monthDto.month, day)
            val type: DayType = when {
                day in parsed.preHoliday -> DayType.PreHoliday(typeText = "")
                day in parsed.transferredDaysOff -> DayType.AdditionalDayOff(typeText = "")
                day in parsed.nonWorking && !date.isDayOffByWeek() ->
                    DayType.NationalHoliday(typeText = "")

                day in parsed.nonWorking -> DayType.Weekend
                date.isDayOffByWeek() -> DayType.Weekend
                else -> DayType.WorkingDay
            }
            ProdCalendarDay(
                date = date,
                type = type,
                note = "",
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
