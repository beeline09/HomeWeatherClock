package ru.weatherclock.adg.app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.dto.forecast.DailyForecastDto
import ru.weatherclock.adg.app.data.dto.forecast.HeadlineDto
import ru.weatherclock.adg.app.data.dto.forecast.asDomainModel
import ru.weatherclock.adg.app.domain.model.Forecast

@Serializable
data class ForecastDto(

    /**
     * Наиболее значительное погодное событие в течение следующих 5 дней.
     */
    @SerialName("Headline")
    val headline: HeadlineDto,

    /**
     * Подробный прогноз на несколько дней
     */
    @SerialName("DailyForecasts")
    val dailyForecasts: List<DailyForecastDto> = emptyList()
)

fun ForecastDto.asDomainModel() = Forecast(
    headline = headline.asDomainModel(),
    dailyForecasts = dailyForecasts.map { it.asDomainModel() }
)














