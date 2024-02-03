package ru.weatherclock.adg.app.domain.model.forecast

data class Wind(
    /**
     * Скорость ветра
     */
    val speed: UnitInfo,

    /**
     * Направлени ветра
     */
    val direction: Direction
)

fun Wind.asDbModelWind(detailPid: Long): ru.weatherclock.adg.db.Accuweather.Wind {
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

fun Wind.asDbModelWindGust(detailPid: Long): ru.weatherclock.adg.db.Accuweather.WindGust {
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

fun ru.weatherclock.adg.db.Accuweather.Wind.asDomainModel(): Wind {
    return Wind(
        speed = UnitInfo(
            value = speed_value,
            phrase = speed_phrase,
            unit = speed_unit,
            unitType = speed_unit_type
        ),
        direction = Direction(
            degrees = direction_degrees,
            localized = direction_localized,
            english = direction_english
        )
    )
}

fun ru.weatherclock.adg.db.Accuweather.WindGust.asDomainModel(): Wind {
    return Wind(
        speed = UnitInfo(
            value = speed_value,
            phrase = speed_phrase,
            unit = speed_unit,
            unitType = speed_unit_type
        ),
        direction = Direction(
            degrees = direction_degrees,
            localized = direction_localized,
            english = direction_english
        )
    )
}