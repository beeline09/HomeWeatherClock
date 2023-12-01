package ru.weatherclock.adg.app.data.dto.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.forecast.DailyForecast

@Serializable
data class DailyForecastDto(
    @SerialName("EpochDate")
    val date: Long,
    @SerialName("Sun")
    val sun: SunDto,
    @SerialName("Moon")
    val moon: MoonDto,

    /**
     * Минимальная и максимальная температура
     */
    @SerialName("Temperature")
    val temperature: TemperatureDto,

    /**
     * Ощущается как
     */
    @SerialName("RealFeelTemperature")
    val realFeelTemperature: TemperatureDto,

    /**
     * Ощущется как. В тени
     */
    @SerialName("RealFeelTemperatureShade")
    val realFeelTemperatureShade: TemperatureDto,

    /**
     * Количество солнечных часов.
     *
     */
    @SerialName("HoursOfSun")
    val hoursOfSun: Double,

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
    val airAndPollen: AirAndPollenDto,

    /**
     * Дневная информация
     */
    @SerialName("Day")
    val day: DetailDto,

    /**
     * Ночная информация
     */
    @SerialName("Night")
    val night: DetailDto,
)

fun DailyForecastDto.asDomainModel(): DailyForecast = DailyForecast(
    date = date,
    sun = sun.asDomainModel(),
    moon = moon.asDomainModel(),
    temperature = temperature.asDomainModel(),
    realFeelTemperature = realFeelTemperature.asDomainModel(),
    realFeelTemperatureShade = realFeelTemperatureShade.asDomainModel(),
    hoursOfSun = hoursOfSun,
    degreeDaySummary = degreeDaySummary?.asDomainModel(),
    airAndPollen = airAndPollen.asDomainModel(),
    day = day.asDomainModel(),
    night = night.asDomainModel()
)