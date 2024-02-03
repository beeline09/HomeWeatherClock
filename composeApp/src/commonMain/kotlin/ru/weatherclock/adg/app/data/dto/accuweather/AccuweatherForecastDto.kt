package ru.weatherclock.adg.app.data.dto.accuweather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.dto.accuweather.forecast.DailyForecastDto
import ru.weatherclock.adg.app.data.dto.accuweather.forecast.HeadlineDto
import ru.weatherclock.adg.app.data.dto.accuweather.forecast.asDomainModel
import ru.weatherclock.adg.app.domain.model.Forecast

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
)

fun AccuweatherForecastDto.asDomainModel() = Forecast(
    headline = headline?.asDomainModel(),
    dailyForecasts = dailyForecasts.map { it.asDomainModel() }
)














