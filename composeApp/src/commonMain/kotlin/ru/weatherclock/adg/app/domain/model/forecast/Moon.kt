package ru.weatherclock.adg.app.domain.model.forecast

import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.app.data.util.fromDbToLocalDateTime
import ru.weatherclock.adg.app.data.util.toDbFormat

data class Moon(
    /**
     * Восход
     */
    val rise: LocalDateTime,

    /**
     * Закат
     */
    val set: LocalDateTime,

    /**
     * Фаза
     */
    val phase: String,

    /**
     * Число дней после новолуния.
     */
    val age: Int
)

fun Moon.asDbModel(forecastPid: Long): ru.weatherclock.adg.db.Moon {
    return ru.weatherclock.adg.db.Moon(
        forecast_pid = forecastPid,
        rise_date_time = rise.toDbFormat(),
        set_date_time = set.toDbFormat(),
        phase = phase,
        age = age,
        pid = -1L
    )
}

fun ru.weatherclock.adg.db.Moon.asDomainModel(): Moon {
    return Moon(
        rise = rise_date_time.fromDbToLocalDateTime(),
        set = set_date_time.fromDbToLocalDateTime(),
        phase = phase.orEmpty(),
        age = age
    )
}
