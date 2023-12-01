package ru.weatherclock.adg.app.domain.model

import ru.weatherclock.adg.app.domain.model.forecast.DailyForecast
import ru.weatherclock.adg.app.domain.model.forecast.Headline

data class Forecast(

    /**
     * Наиболее значительное погодное событие в течение следующих 5 дней.
     */
    val headline: Headline,

    /**
     * Подробный прогноз на несколько дней
     */
    val dailyForecasts: List<DailyForecast> = emptyList()
)














