package ru.weatherclock.adg.app.presentation.components.weather

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.weatherclock.adg.app.domain.model.forecast.DailyForecast
import ru.weatherclock.adg.app.domain.model.forecast.Detail
import ru.weatherclock.adg.app.domain.model.forecast.DetailType
import ru.weatherclock.adg.app.domain.model.forecast.Direction
import ru.weatherclock.adg.app.domain.model.forecast.Moon
import ru.weatherclock.adg.app.domain.model.forecast.Sun
import ru.weatherclock.adg.app.domain.model.forecast.Temperature
import ru.weatherclock.adg.app.domain.model.forecast.UnitInfo
import ru.weatherclock.adg.app.domain.model.forecast.Wind
import ru.weatherclock.adg.theme.AppTheme

@Composable
@Preview(
    widthDp = 220,
    heightDp = 150,
    locale = "ru"
)
fun WeatherCellPreview() {
    AppTheme {
        Column(
            modifier = Modifier
//                .background(color = Color.Green)
                .fillMaxSize()
        ) {
            WeatherCell(
                isPreview = true,
                forecast = DailyForecast(
                    airAndPollen = null,
                    sun = Sun(
                        rise = LocalDateTime(
                            2024,
                            1,
                            15,
                            6,
                            1,
                            0
                        ),
                        set = LocalDateTime(
                            2024,
                            1,
                            15,
                            17,
                            1,
                            0
                        ),
                    ),
                    moon = Moon(
                        rise = LocalDateTime(
                            2024,
                            1,
                            15,
                            19,
                            1,
                            0
                        ),
                        set = LocalDateTime(
                            2024,
                            1,
                            15,
                            11,
                            1,
                            0
                        ),
                        phase = "",
                        age = 11
                    ),
                    date = LocalDate(
                        2024,
                        1,
                        15
                    ),
                    hoursOfSun = 1.8,
                    degreeDaySummary = null,
                    temperature = Temperature(
                        minimum = UnitInfo(
                            value = -28.4,
                            unit = "C",
                            unitType = 0,
                            phrase = null
                        ),
                        maximum = UnitInfo(
                            value = 1.3,
                            unit = "C",
                            unitType = 0,
                            phrase = null
                        )
                    ),
                    realFeelTemperature = Temperature(
                        minimum = UnitInfo(
                            value = -11.2,
                            unit = "C",
                            unitType = 0,
                            phrase = null
                        ),
                        maximum = UnitInfo(
                            value = 0.1,
                            unit = "C",
                            unitType = 0,
                            phrase = null
                        )
                    ),
                    realFeelTemperatureShade = null,
                    day = Detail(
                        icon = 6,
                        iconPhrase = "Преимущественно облачно",
                        shortPhrase = "Холоднее",
                        longPhrase = "Холоднее",
                        precipitationProbability = 4,
                        thunderstormProbability = 0,
                        rainProbability = 1,
                        snowProbability = 2,
                        iceProbability = 0,
                        wind = Wind(
                            speed = UnitInfo(
                                value = 3.7,
                                unit = "km/h",
                                unitType = 7,
                            ),
                            direction = Direction(
                                degrees = 318.0,
                                localized = "СЗ",
                                english = "NW"
                            )
                        ),
                        windGust = Wind(
                            speed = UnitInfo(
                                value = 13.0,
                                unit = "km/h",
                                unitType = 7,
                            ),
                            direction = Direction(
                                degrees = 211.0,
                                localized = "ЮЮЗ",
                                english = "SSW"
                            )
                        ),
                        totalLiquid = UnitInfo(
                            value = 0.0,
                            unitType = 3,
                            unit = "mm"
                        ),
                        rain = UnitInfo(
                            value = 0.0,
                            unitType = 3,
                            unit = "mm"
                        ),
                        snow = UnitInfo(
                            value = 0.0,
                            unitType = 4,
                            unit = "cm"
                        ),
                        ice = UnitInfo(
                            value = 0.0,
                            unitType = 3,
                            unit = "mm"
                        ),
                        hoursOfPrecipitation = 0.0,
                        cloudCover = 71,
                        hoursOfRain = 0.0,
                        hoursOfSnow = 0.0,
                        hoursOfIce = 0.0,
                        evapotranspiration = UnitInfo(
                            value = 0.5,
                            unitType = 3,
                            unit = "mm"
                        ),
                        solarIrradiance = UnitInfo(
                            value = 674.9,
                            unitType = 33,
                            unit = "W/m²"
                        ),
                        detailType = DetailType.DAY,
                        precipitationIntensity = null,
                        hasPrecipitation = false,
                        precipitationType = null
                    ),
                    night = Detail(
                        icon = 33,
                        iconPhrase = "Ясно",
                        shortPhrase = "Ясно",
                        longPhrase = "Ясно",
                        precipitationProbability = 1,
                        thunderstormProbability = 0,
                        rainProbability = 0,
                        snowProbability = 1,
                        iceProbability = 0,
                        wind = Wind(
                            speed = UnitInfo(
                                value = 11.1,
                                unit = "km/h",
                                unitType = 7,
                            ),
                            direction = Direction(
                                degrees = 89.0,
                                localized = "В",
                                english = "E"
                            )
                        ),
                        windGust = Wind(
                            speed = UnitInfo(
                                value = 18.5,
                                unit = "km/h",
                                unitType = 7,
                            ),
                            direction = Direction(
                                degrees = 113.0,
                                localized = "ВЮВ",
                                english = "ESE"
                            )
                        ),
                        totalLiquid = UnitInfo(
                            value = 0.0,
                            unitType = 3,
                            unit = "mm"
                        ),
                        rain = UnitInfo(
                            value = 0.0,
                            unitType = 3,
                            unit = "mm"
                        ),
                        snow = UnitInfo(
                            value = 0.0,
                            unitType = 4,
                            unit = "cm"
                        ),
                        ice = UnitInfo(
                            value = 0.0,
                            unitType = 3,
                            unit = "mm"
                        ),
                        hoursOfPrecipitation = 0.0,
                        cloudCover = 11,
                        hoursOfRain = 0.0,
                        hoursOfSnow = 0.0,
                        hoursOfIce = 0.0,
                        evapotranspiration = UnitInfo(
                            value = 0.5,
                            unitType = 3,
                            unit = "mm"
                        ),
                        solarIrradiance = UnitInfo(
                            value = 674.9,
                            unitType = 33,
                            unit = "W/m²"
                        ),
                        detailType = DetailType.DAY,
                        precipitationIntensity = null,
                        hasPrecipitation = false,
                        precipitationType = null
                    )
                )
            )
        }
    }
}