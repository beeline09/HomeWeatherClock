package ru.weatherclock.adg.app.data.dto.forecast.accuweather.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WindDto(
    /**
     * Скорость ветра
     */
    @SerialName("Speed")
    val speed: UnitDto = UnitDto(),

    /**
     * Направлени ветра
     */
    @SerialName("Direction")
    val direction: DirectionDto = DirectionDto()
)

fun WindDto.asAccuweatherDbModelWind(detailPid: Long): ru.weatherclock.adg.db.Accuweather.Wind {
    return ru.weatherclock.adg.db.Accuweather.Wind(
        speed_phrase = speed.phrase,
        speed_unit = speed.unit,
        speed_unit_type = speed.unitType,
        speed_value = speed.value,
        direction_degrees = direction.degrees,
        direction_english = direction.english,
        direction_localized = direction.localized,
        detail_pid = detailPid,
        pid = -1L
    )
}

fun WindDto.asAccuweatherDbModelWindGust(detailPid: Long): ru.weatherclock.adg.db.Accuweather.WindGust {
    return ru.weatherclock.adg.db.Accuweather.WindGust(
        speed_phrase = speed.phrase,
        speed_unit = speed.unit,
        speed_unit_type = speed.unitType,
        speed_value = speed.value,
        direction_degrees = direction.degrees,
        direction_english = direction.english,
        direction_localized = direction.localized,
        detail_pid = detailPid,
        pid = -1L
    )
}