package ru.weatherclock.adg.app.data.dto.forecast.accuweather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.dto.forecast.ForecastDto
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.DailyForecastDto
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.HeadlineDto

@Serializable
data class AccuweatherForecastDto(

    /**
     * Наиболее значительное погодное событие в течение следующих 5 дней.
     */
    @SerialName("Headline")
    val headline: HeadlineDto? = null,

    /**
     * Подробный прогноз на несколько дней
     */
    @SerialName("DailyForecasts")
    val dailyForecasts: List<DailyForecastDto> = emptyList()
): ForecastDto














