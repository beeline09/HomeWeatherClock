package ru.weatherclock.adg.app.domain.model.forecast

import kotlinx.datetime.LocalDate
import ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail.DailyForecastDto
import ru.weatherclock.adg.app.data.util.epochSecondsToLocalDate

data class ForecastDay(
    val date: LocalDate,
    val min: DayDetail = DayDetail(),
    val max: DayDetail = DayDetail()
)

fun DailyForecastDto.asDomainModel(): ForecastDay = ForecastDay(
    date = date.epochSecondsToLocalDate(),
    max = DayDetail(
        temperature = temperature?.maximum?.value ?: 0.0,
        icon = day?.icon?.toString().orEmpty(),
        iconPhrase = day?.iconPhrase
    ),
    min = DayDetail(
        temperature = temperature?.minimum?.value ?: 0.0,
        icon = night?.icon?.toString().orEmpty(),
        iconPhrase = night?.iconPhrase
    )
)
