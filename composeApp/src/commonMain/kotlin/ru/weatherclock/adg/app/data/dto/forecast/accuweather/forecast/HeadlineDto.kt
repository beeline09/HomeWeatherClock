package ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.util.epochSecondsToLocalDate
import ru.weatherclock.adg.app.data.util.toDbFormat
import ru.weatherclock.adg.app.domain.util.UNSPECIFIED_DATE
import ru.weatherclock.adg.db.Accuweather.ForecastHeadline

/**
 * Наиболее значительное погодное событие в течение следующих 5 дней.
 */
@Serializable
data class HeadlineDto(
    @SerialName("EffectiveEpochDate")
    val startDate: Long = UNSPECIFIED_DATE,

    /**
     * Серьезность заголовка, отображаемая как целое число.
     * Чем меньше число, тем выше серьезность. 0 = Неизвестно, 1 = Значительно,
     * 2 = Значительно, 3 = Умеренно, 4 = Незначительно, 5 = Минимально,
     * 6 = Незначительно, 7 = Информационно
     */
    @SerialName("Severity")
    val severity: Int = 0,

    /**
     * Текст заголовка, который представляет наиболее значительное погодное событие
     * в течение следующих 5 дней. Отображается на языке, указанном кодом языка в URL-адресе.
     */
    @SerialName("Text")
    val text: String = "",

    /**
     * Категория заголовка.
     */
    @SerialName("Category")
    val category: String = "",
    @SerialName("EndEpochDate")
    val endDate: Long = UNSPECIFIED_DATE,
)

fun HeadlineDto.asAccuweatherDbModel(forecastKey: String): ForecastHeadline {
    return ForecastHeadline(
        start_date = startDate.epochSecondsToLocalDate().toDbFormat(),
        end_date = endDate.epochSecondsToLocalDate().toDbFormat(),
        text = text,
        category = category,
        severity = severity,
        forecast_key = forecastKey,
        pid = -1L
    )
}
