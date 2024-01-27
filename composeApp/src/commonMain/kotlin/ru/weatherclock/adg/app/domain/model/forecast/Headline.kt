package ru.weatherclock.adg.app.domain.model.forecast

import kotlinx.datetime.LocalDate
import ru.weatherclock.adg.app.data.util.fromDbToLocalDate
import ru.weatherclock.adg.app.data.util.toDbFormat
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
        start_date = startDate.toDbFormat(),
        end_date = endDate.toDbFormat(),
        text = text,
        category = category,
        severity = severity.ordinal,
        forecast_key = forecastKey,
        pid = -1L
    )
}

fun ForecastHeadline.asDomainModel(): Headline {
    return Headline(
        startDate = start_date.fromDbToLocalDate(),
        endDate = end_date.fromDbToLocalDate(),
        text = text.orEmpty(),
        category = category.orEmpty(),
        severity = Severity.entries[severity]
    )
}
