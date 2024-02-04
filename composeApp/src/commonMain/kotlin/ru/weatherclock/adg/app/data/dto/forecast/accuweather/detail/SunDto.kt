package ru.weatherclock.adg.app.data.dto.forecast.accuweather.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.data.util.epochSecondsToLocalDateTime
import ru.weatherclock.adg.app.data.util.toDbFormat

@Serializable
data class SunDto(
    /**
     * Восход
     */
    @SerialName("EpochRise")
    val rise: Long? = null,

    /**
     * Закат
     */
    @SerialName("EpochSet")
    val set: Long? = null
)

fun SunDto.asAccuweatherDbModel(forecastPid: Long): ru.weatherclock.adg.db.Accuweather.Sun {
    return ru.weatherclock.adg.db.Accuweather.Sun(
        forecast_pid = forecastPid,
        rise_date_time = rise?.epochSecondsToLocalDateTime()?.toDbFormat(),
        set_date_time = set?.epochSecondsToLocalDateTime()?.toDbFormat(),
        pid = -1L
    )
}
