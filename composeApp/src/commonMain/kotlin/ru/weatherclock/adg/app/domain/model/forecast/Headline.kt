package ru.weatherclock.adg.app.domain.model.forecast

import kotlinx.datetime.LocalDate
import ru.weatherclock.adg.db.ForecastHeadline

/**
 * Наиболее значительное погодное событие в течение следующих 5 дней.
 */
data class Headline(

    val startDate: LocalDate,
    val severity: Severity,

    /**
     * Текст заголовка, который представляет наиболее значительное погодное событие
     * в течение следующих 5 дней. Отображается на языке, указанном кодом языка в URL-адресе.
     */
    val text: String,

    /**
     * Категория заголовка.
     */
    val category: String,
    val endDate: LocalDate,
)

fun Headline.asDbModel(forecastKey: String): ForecastHeadline {
    return ForecastHeadline(
        start_year = startDate.year,
        start_month = startDate.monthNumber,
        start_day_of_month = startDate.dayOfMonth,
        end_year = endDate.year,
        end_month = endDate.monthNumber,
        end_day_of_month = endDate.dayOfMonth,
        text = text,
        category = category,
        severity = severity.ordinal,
        forecast_key = forecastKey,
        pid = -1L
    )
}

fun ForecastHeadline.asDomainModel(): Headline {
    return Headline(
        startDate = LocalDate(
            start_year,
            start_month,
            start_day_of_month
        ),
        endDate = LocalDate(
            end_year,
            end_month,
            end_day_of_month
        ),
        text = text.orEmpty(),
        category = category.orEmpty(),
        severity = Severity.entries[severity]
    )
}
