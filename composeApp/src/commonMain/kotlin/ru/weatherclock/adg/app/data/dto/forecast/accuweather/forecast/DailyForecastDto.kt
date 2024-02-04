package ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.util.epochSecondsToLocalDate
import ru.weatherclock.adg.app.data.util.fromDbToLocalDate
import ru.weatherclock.adg.app.data.util.toDbFormat
import ru.weatherclock.adg.app.domain.model.forecast.DayDetail
import ru.weatherclock.adg.app.domain.model.forecast.ForecastDay
import ru.weatherclock.adg.db.Accuweather.DailyForecast
import ru.weatherclock.adg.db.Accuweather.ForecastDetail
import ru.weatherclock.adg.db.Accuweather.Temperature

@Serializable
data class DailyForecastDto(
    @SerialName("EpochDate")
    val date: Long,
    @SerialName("Sun")
    val sun: SunDto? = null,
    @SerialName("Moon")
    val moon: MoonDto? = null,

    /**
     * Минимальная и максимальная температура
     */
    @SerialName("Temperature")
    val temperature: TemperatureDto? = null,

    /**
     * Ощущается как
     */
    @SerialName("RealFeelTemperature")
    val realFeelTemperature: TemperatureDto? = null,

    /**
     * Ощущется как. В тени
     */
    @SerialName("RealFeelTemperatureShade")
    val realFeelTemperatureShade: TemperatureDto? = null,

    /**
     * Количество солнечных часов.
     *
     */
    @SerialName("HoursOfSun")
    val hoursOfSun: Double = 0.0,

    /**
     * Количество градусов, на которое средняя температура ниже 65 градусов по Фаренгейту.
     * Отображается в указанных единицах. Может быть НУЛЬ.
     */
    @SerialName("DegreeDaySummary")
    val degreeDaySummary: DaySummaryDto? = null,

    /**
     * Загрязнение и пыльца
     */
    @SerialName("AirAndPollen")
    val airAndPollen: List<AirAndPollenDto>? = null,

    /**
     * Дневная информация
     */
    @SerialName("Day")
    val day: DetailDto? = null,

    /**
     * Ночная информация
     */
    @SerialName("Night")
    val night: DetailDto? = null,
)

fun DailyForecastDto.asAccuweatherDbModel(forecastKey: String): ru.weatherclock.adg.db.Accuweather.DailyForecast {
    return ru.weatherclock.adg.db.Accuweather.DailyForecast(
        date = date.epochSecondsToLocalDate().toDbFormat(),
        hours_of_sun = hoursOfSun,
        forecast_key = forecastKey,
        pid = -1L
    )
}

fun DailyForecast.asDomainModel(
    detailDay: ForecastDetail?,
    detailNight: ForecastDetail?,
    temperature: Temperature?
): ForecastDay? = if (temperature == null || detailDay == null || detailNight == null) {
    null
} else ForecastDay(
    date = date.fromDbToLocalDate(),
    max = DayDetail(
        temperature = temperature.max_value,
        icon = detailDay.icon.toString(),
        iconPhrase = detailDay.icon_phrase
    ),
    min = DayDetail(
        temperature = temperature.min_value,
        icon = detailNight.icon.toString(),
        iconPhrase = detailNight.icon_phrase
    )
)