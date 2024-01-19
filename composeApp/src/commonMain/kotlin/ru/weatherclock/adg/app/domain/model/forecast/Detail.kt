package ru.weatherclock.adg.app.domain.model.forecast

import ru.weatherclock.adg.db.ForecastDetail

data class Detail(
    /**
     * Иконка погоды
     */
    val icon: Int,

    /**
     * Фразовое описание значка. Например, **Ливни**
     */
    val iconPhrase: String,

    /**
     * Есть осадки
     */
    val hasPrecipitation: Boolean,

    /**
     * Указывает, являются ли осадки дождем, снегом, льдом или смешанными.
     * Возвращается только в том случае, если HasPrecipitation имеет значение true
     */
    val precipitationType: String? = null,

    /**
     * Указывает, является ли интенсивность осадков легкой, средней или сильной.
     * Возвращается только в том случае, если HasPrecipitation имеет значение true.
     */
    val precipitationIntensity: String? = null,

    /**
     * Описание прогноза. AccuWeather старается, чтобы длина этой фразы не превышала 30 символов,
     * но в зависимости от языка/погоды длина фразы может превышать 30 символов.
     */
    val shortPhrase: String,

    /**
     * Описание прогноза. AccuWeather старается, чтобы длина этой фразы не превышала 100 символов,
     * но в зависимости от языка или погодных явлений длина фразы может превышать 100 символов.
     */
    val longPhrase: String,

    /**
     * Процент, представляющий вероятность осадков. Может быть НУЛЬ.
     */
    val precipitationProbability: Int? = null,

    /**
     * Процент, представляющий вероятность грозы. Может быть НУЛЬ.
     */
    val thunderstormProbability: Int? = null,

    /**
     * Процент, представляющий вероятность дождя. Может быть НУЛЬ.
     */
    val rainProbability: Int? = null,

    /**
     * Процент, представляющий вероятность снега. Может быть НУЛЬ.
     */
    val snowProbability: Int? = null,

    /**
     * Процент, представляющий вероятность образования льда. Может быть НУЛЬ.
     */
    val iceProbability: Int? = null,

    /**
     * Ветер
     */
    val wind: Wind?,

    /**
     * Порывы ветра
     */
    val windGust: Wind?,

    /**
     * Всего осадков
     */
    val totalLiquid: UnitInfo?,

    /**
     * Колчество осадков для дождя
     */
    val rain: UnitInfo?,

    /**
     * Колчество осадков для снега
     */
    val snow: UnitInfo?,

    /**
     * Колчество осадков для льда
     */
    val ice: UnitInfo?,

    /**
     * Кол-во часов, в течение которых будут идти осадки
     */
    val hoursOfPrecipitation: Double,

    /**
     * Кол-во часов, в течение которых будет дождь
     */
    val hoursOfRain: Double,

    /**
     * Кол-во часов, в течение которых будет снег
     */
    val hoursOfSnow: Double,

    /**
     * Кол-во часов, в течение которых будет лёд
     */
    val hoursOfIce: Double,

    /**
     * Облачность
     */
    val cloudCover: Int,

    /**
     * Хуйня какая-то непонятная
     */
    val evapotranspiration: UnitInfo?,

    /**
     * Ну тут всё понятно - солнечная радиация
     */
    val solarIrradiance: UnitInfo?,

    var detailType: DetailType = DetailType.DAY
)

fun Detail.asDbModel(forecastPid: Long): ForecastDetail {
    return ForecastDetail(
        forecast_pid = forecastPid,
        icon = icon,
        icon_phrase = iconPhrase,
        has_precipitation = hasPrecipitation,
        precipitation_intensity = precipitationIntensity,
        precipitation_probability = precipitationProbability,
        precipitation_type = precipitationType,
        hours_of_precipitation = hoursOfPrecipitation,
        cloud_cover = cloudCover,
        hours_of_ice = hoursOfIce,
        hours_of_rain = hoursOfRain,
        hours_of_snow = hoursOfSnow,
        short_phrase = shortPhrase,
        ice_probability = iceProbability,
        snow_probability = snowProbability,
        rain_probability = rainProbability,
        is_night = detailType == DetailType.NIGHT,
        long_phrase = longPhrase,
        thunderstorm_probability = thunderstormProbability,
        pid = -1L
    )
}

fun ForecastDetail.asDomainModel(
    evapotranspiration: UnitInfo?,
    solarIrradiance: UnitInfo?,
    wind: Wind?,
    windGust: Wind?,
    totalLiquid: UnitInfo?,
    rain: UnitInfo?,
    snow: UnitInfo?,
    ice: UnitInfo?,
): Detail {
    return Detail(
        icon = icon,
        iconPhrase = icon_phrase,
        hasPrecipitation = has_precipitation,
        precipitationType = precipitation_type,
        precipitationIntensity = precipitation_intensity,
        precipitationProbability = precipitation_probability,
        hoursOfPrecipitation = hours_of_precipitation,
        cloudCover = cloud_cover,
        hoursOfIce = hours_of_ice,
        hoursOfRain = hours_of_rain,
        hoursOfSnow = hours_of_snow,
        shortPhrase = short_phrase,
        iceProbability = ice_probability,
        snowProbability = snow_probability,
        rainProbability = rain_probability,
        detailType = if (is_night) DetailType.NIGHT else DetailType.DAY,
        longPhrase = long_phrase,
        thunderstormProbability = thunderstorm_probability,
        evapotranspiration = evapotranspiration,
        ice = ice,
        rain = rain,
        snow = snow,
        solarIrradiance = solarIrradiance,
        totalLiquid = totalLiquid,
        wind = wind,
        windGust = windGust
    )
}

fun Detail.flatSvgIconUrl(): String =
    "https://vortex.accuweather.com/adc2010/images/slate/icons/${icon}.svg"

fun Detail.accuweatherPngIconUrl(): String =
    "https://vortex.accuweather.com/adc2010/images/slate/icons/${icon}.svg"

