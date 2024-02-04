package ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.util.epochSecondsToLocalDateTime
import ru.weatherclock.adg.app.data.util.toDbFormat
import ru.weatherclock.adg.app.domain.util.UNSPECIFIED_DATE

@Serializable
data class MoonDto(
    /**
     * Восход
     */
    @SerialName("EpochRise")
    val rise: Long? = UNSPECIFIED_DATE,

    /**
     * Закат
     */
    @SerialName("EpochSet")
    val set: Long? = UNSPECIFIED_DATE,

    /**
     * Фаза
     */
    @SerialName("Phase")
    val phase: String = "",

    /**
     * Число дней после новолуния.
     */
    @SerialName("Age")
    val age: Int = -1
)

fun MoonDto.asAccuweatherDbModel(forecastPid: Long): ru.weatherclock.adg.db.Accuweather.Moon {
    return ru.weatherclock.adg.db.Accuweather.Moon(
        forecast_pid = forecastPid,
        rise_date_time = rise?.epochSecondsToLocalDateTime()?.toDbFormat(),
        set_date_time = set?.epochSecondsToLocalDateTime()?.toDbFormat(),
        phase = phase,
        age = age,
        pid = -1L
    )
}
