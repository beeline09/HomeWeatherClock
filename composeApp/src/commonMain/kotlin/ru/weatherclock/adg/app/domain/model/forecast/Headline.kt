package ru.weatherclock.adg.app.domain.model.forecast

/**
 * Наиболее значительное погодное событие в течение следующих 5 дней.
 */
data class Headline(

    val startDate: Long,
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
    val endDate: Long,
)
