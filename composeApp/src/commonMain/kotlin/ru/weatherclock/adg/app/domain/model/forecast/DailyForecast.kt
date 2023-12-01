package ru.weatherclock.adg.app.domain.model.forecast

data class DailyForecast(
    val date: Long,
    val sun: Sun,
    val moon: Moon,

    /**
     * Минимальная и максимальная температура
     */
    val temperature: Temperature,

    /**
     * Ощущается как
     */
    val realFeelTemperature: Temperature,

    /**
     * Ощущется как. В тени
     */
    val realFeelTemperatureShade: Temperature,

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
    val airAndPollen: AirAndPollen,

    /**
     * Дневная информация
     */
    val day: Detail,

    /**
     * Ночная информация
     */
    val night: Detail,
)
