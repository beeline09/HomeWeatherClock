package ru.weatherclock.adg.app.data.dto.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.forecast.Headline
import ru.weatherclock.adg.app.domain.model.forecast.Severity
import ru.weatherclock.adg.app.domain.util.UNSPECIFIED_DATE

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

fun HeadlineDto.asDomainModel() = Headline(
    startDate = startDate,
    text = text,
    category = category,
    endDate = endDate,
    severity = when (severity) {
        7 -> Severity.INFORMATIONAL
        6 -> Severity.INSIGNIFICANT
        5 -> Severity.MINIMAL
        4 -> Severity.MINOR
        3 -> Severity.MODERATE
        2 -> Severity.MAJOR
        1 -> Severity.SIGNIFICANT
        else -> Severity.UNKNOWN
    }
)
