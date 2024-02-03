package ru.weatherclock.adg.app.data.dto.accuweather.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.util.epochSecondsToLocalDate
import ru.weatherclock.adg.app.domain.model.forecast.DailyForecast

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

fun DailyForecastDto.asDomainModel(): DailyForecast = DailyForecast(
    date = date.epochSecondsToLocalDate(),
    sun = sun?.asDomainModel(),
    moon = moon?.asDomainModel(),
    temperature = temperature?.asDomainModel(),
    realFeelTemperature = realFeelTemperature?.asDomainModel(),
    realFeelTemperatureShade = realFeelTemperatureShade?.asDomainModel(),
    hoursOfSun = hoursOfSun,
    degreeDaySummary = degreeDaySummary?.asDomainModel(),
    airAndPollen = airAndPollen?.map { it.asDomainModel() },
    day = day?.asDomainModel(),
    night = night?.asDomainModel()
)