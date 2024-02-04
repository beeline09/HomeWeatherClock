package ru.weatherclock.adg.app.domain.model.forecast

import ru.weatherclock.adg.app.data.dto.forecast.accuweather.AccuweatherForecastDto
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.OpenWeatherMapForecastDto
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.forecast.PartOfDay
import ru.weatherclock.adg.app.data.util.epochSecondsToLocalDateTime

data class Forecast(
    val headline: String? = null,
    val severity: Severity = Severity.UNKNOWN,
    val dailyForecast: List<ForecastDay> = emptyList()
)

fun AccuweatherForecastDto.asDomainModel(): Forecast = Forecast(
    headline = headline?.text,
    dailyForecast = dailyForecasts.map { it.asDomainModel() }
)

fun OpenWeatherMapForecastDto.asDomainModel(): Forecast {
    val byDays = items.groupBy {
        it.timeStamp.epochSecondsToLocalDateTime().date
    }.filter { it.value.size >= 2 }.mapNotNull {
        val date = it.key
        val dayValues = it.value.filter { item -> item.sys.partOfDay == PartOfDay.Day }
        val nightValues = it.value.filter { item -> item.sys.partOfDay == PartOfDay.Night }
        if (dayValues.isEmpty() || nightValues.isEmpty()) {
            null
        } else {
            val night = nightValues.minBy { item -> item.main.temperature }
            val day = dayValues.maxBy { item -> item.main.temperature }
            ForecastDay(
                date = date,
                min = DayDetail(
                    temperature = night.main.temperature,
                    icon = night.weather.firstOrNull()?.icon.orEmpty(),
                    iconPhrase = night.weather.firstOrNull()?.description
                ),
                max = DayDetail(
                    temperature = day.main.temperature,
                    icon = day.weather.firstOrNull()?.icon.orEmpty(),
                    iconPhrase = day.weather.firstOrNull()?.description
                )
            )
        }

    }
    return Forecast(dailyForecast = byDays)
}
