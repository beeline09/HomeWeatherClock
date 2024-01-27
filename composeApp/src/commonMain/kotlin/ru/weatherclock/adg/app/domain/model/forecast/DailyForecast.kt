package ru.weatherclock.adg.app.domain.model.forecast

import kotlinx.datetime.LocalDate
import ru.weatherclock.adg.app.data.util.fromDbToLocalDate
import ru.weatherclock.adg.app.data.util.toDbFormat

data class DailyForecast(
    val date: LocalDate,
    val sun: Sun?,
    val moon: Moon?,

    /**
     * Минимальная и максимальная температура
     */
    val temperature: Temperature?,

    /**
     * Ощущается как
     */
    val realFeelTemperature: Temperature?,

    /**
     * Ощущется как. В тени
     */
    val realFeelTemperatureShade: Temperature?,

    /**
     * Количество солнечных часов.
     *
     */
    val hoursOfSun: Double,

    /**
     * Количество градусов, на которое средняя температура ниже 65 градусов по Фаренгейту.
     * Отображается в указанных единицах. Может быть НУЛЬ.
     */
    val degreeDaySummary: DaySummary? = null,

    /**
     * Загрязнение и пыльца
     */
    val airAndPollen: List<AirAndPollen>?,

    /**
     * Дневная информация
     */
    val day: Detail?,

    /**
     * Ночная информация
     */
    val night: Detail?,
) {

    init {
        day?.detailType = DetailType.DAY
        night?.detailType = DetailType.NIGHT
    }
}

fun DailyForecast.asDbModel(forecastKey: String): ru.weatherclock.adg.db.DailyForecast {
    return ru.weatherclock.adg.db.DailyForecast(
        date = date.toDbFormat(),
        hours_of_sun = hoursOfSun,
        forecast_key = forecastKey,
        pid = -1L
    )
}

fun ru.weatherclock.adg.db.DailyForecast.asDomainModel(
    temperature: Temperature?,
    realFeelTemperature: Temperature?,
    realFeelTemperatureShade: Temperature?,
    degreeDaySummary: DaySummary?,
    airAndPollen: List<AirAndPollen>?,
    day: Detail?,
    night: Detail?,
    sun: Sun?,
    moon: Moon?,
): DailyForecast {
    return DailyForecast(
        date = date.fromDbToLocalDate(),
        hoursOfSun = hours_of_sun,
        airAndPollen = airAndPollen,
        temperature = temperature,
        realFeelTemperature = realFeelTemperature,
        realFeelTemperatureShade = realFeelTemperatureShade,
        day = day,
        night = night,
        degreeDaySummary = degreeDaySummary,
        sun = sun,
        moon = moon
    )
}
