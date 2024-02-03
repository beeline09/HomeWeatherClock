package ru.weatherclock.adg.app.domain.model.forecast

import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.app.data.util.fromDbToLocalDateTime
import ru.weatherclock.adg.app.data.util.toDbFormat

data class Sun(
    /**
     * Восход
     */
    val rise: LocalDateTime?,

    /**
     * Закат
     */
    val set: LocalDateTime?
)

fun Sun.asDbModel(forecastPid: Long): ru.weatherclock.adg.db.Accuweather.Sun {
    return ru.weatherclock.adg.db.Accuweather.Sun(
        forecast_pid = forecastPid,
        rise_date_time = rise?.toDbFormat(),
        set_date_time = set?.toDbFormat(),
        pid = -1L
    )
}

fun ru.weatherclock.adg.db.Accuweather.Sun.asDomainModel(): Sun {
    return Sun(
        rise = rise_date_time?.fromDbToLocalDateTime(),
        set = set_date_time?.fromDbToLocalDateTime()
    )
}
