package ru.weatherclock.adg.app.domain.model.forecast

import kotlinx.datetime.LocalDateTime

data class Sun(
    /**
     * Восход
     */
    val rise: LocalDateTime,

    /**
     * Закат
     */
    val set: LocalDateTime
)

fun Sun.asDbModel(forecastPid: Long): ru.weatherclock.adg.db.Sun {
    return ru.weatherclock.adg.db.Sun(
        forecast_pid = forecastPid,
        rise_year = rise.year,
        rise_month = rise.monthNumber,
        rise_day_of_month = rise.dayOfMonth,
        rise_hour = rise.hour,
        rise_minute = rise.minute,
        rise_second = rise.second,
        set_year = set.year,
        set_month = set.monthNumber,
        set_day_of_month = set.dayOfMonth,
        set_hour = set.hour,
        set_minute = set.minute,
        set_second = set.second,
        pid = -1L
    )
}

fun ru.weatherclock.adg.db.Sun.asDomainModel(): Sun {
    return Sun(
        rise = LocalDateTime(
            rise_year,
            rise_month,
            rise_day_of_month,
            rise_hour,
            rise_minute,
            rise_second
        ),
        set = LocalDateTime(
            set_year,
            set_month,
            set_day_of_month,
            set_hour,
            set_minute,
            set_second
        )
    )
}
