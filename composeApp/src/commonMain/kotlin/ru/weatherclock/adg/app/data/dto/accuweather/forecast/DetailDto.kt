package ru.weatherclock.adg.app.data.dto.accuweather.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.forecast.Detail

@Serializable
data class DetailDto(
    /**
     * Иконка погоды
     */
    @SerialName("Icon")
    val icon: Int = -1,

    /**
     * Фразовое описание значка. Например, **Ливни**
     */
    @SerialName("IconPhrase")
    val iconPhrase: String = "",

    /**
     * Есть осадки
     */
    @SerialName("HasPrecipitation")
    val hasPrecipitation: Boolean = false,

    /**
     * Указывает, являются ли осадки дождем, снегом, льдом или смешанными.
     * Возвращается только в том случае, если HasPrecipitation имеет значение true
     */
    @SerialName("PrecipitationType")
    val precipitationType: String? = null,

    /**
     * Указывает, является ли интенсивность осадков легкой, средней или сильной.
     * Возвращается только в том случае, если HasPrecipitation имеет значение true.
     */
    @SerialName("PrecipitationIntensity")
    val precipitationIntensity: String? = null,

    /**
     * Описание прогноза. AccuWeather старается, чтобы длина этой фразы не превышала 30 символов,
     * но в зависимости от языка/погоды длина фразы может превышать 30 символов.
     */
    @SerialName("ShortPhrase")
    val shortPhrase: String = "",

    /**
     * Описание прогноза. AccuWeather старается, чтобы длина этой фразы не превышала 100 символов,
     * но в зависимости от языка или погодных явлений длина фразы может превышать 100 символов.
     */
    @SerialName("LongPhrase")
    val longPhrase: String = "",

    /**
     * Процент, представляющий вероятность осадков. Может быть НУЛЬ.
     */
    @SerialName("PrecipitationProbability")
    val precipitationProbability: Int? = null,

    /**
     * Процент, представляющий вероятность грозы. Может быть НУЛЬ.
     */
    @SerialName("ThunderstormProbability")
    val thunderstormProbability: Int? = null,

    /**
     * Процент, представляющий вероятность дождя. Может быть НУЛЬ.
     */
    @SerialName("RainProbability")
    val rainProbability: Int? = null,

    /**
     * Процент, представляющий вероятность снега. Может быть НУЛЬ.
     */
    @SerialName("SnowProbability")
    val snowProbability: Int? = null,

    /**
     * Процент, представляющий вероятность образования льда. Может быть НУЛЬ.
     */
    @SerialName("IceProbability")
    val iceProbability: Int? = null,

    /**
     * Ветер
     */
    @SerialName("Wind")
    val wind: WindDto? = null,

    /**
     * Порывы ветра
     */
    @SerialName("WindGust")
    val windGust: WindDto? = null,

    /**
     * Всего осадков
     */
    @SerialName("TotalLiquid")
    val totalLiquid: UnitDto? = null,

    /**
     * Колчество осадков для дождя
     */
    @SerialName("Rain")
    val rain: UnitDto? = null,

    /**
     * Колчество осадков для снега
     */
    @SerialName("Snow")
    val snow: UnitDto? = null,

    /**
     * Колчество осадков для льда
     */
    @SerialName("Ice")
    val ice: UnitDto? = null,

    /**
     * Кол-во часов, в течение которых будут идти осадки
     */
    @SerialName("HoursOfPrecipitation")
    val hoursOfPrecipitation: Double = 0.0,

    /**
     * Кол-во часов, в течение которых будет дождь
     */
    @SerialName("HoursOfRain")
    val hoursOfRain: Double = 0.0,

    /**
     * Кол-во часов, в течение которых будет снег
     */
    @SerialName("HoursOfSnow")
    val hoursOfSnow: Double = 0.0,

    /**
     * Кол-во часов, в течение которых будет лёд
     */
    @SerialName("HoursOfIce")
    val hoursOfIce: Double = 0.0,

    /**
     * Облачность
     */
    @SerialName("CloudCover")
    val cloudCover: Int = 0,

    /**
     * Хуйня какая-то непонятная
     */
    @SerialName("Evapotranspiration")
    val evapotranspiration: UnitDto? = null,

    /**
     * Ну тут всё понятно - солнечная радиация
     */
    @SerialName("SolarIrradiance")
    val solarIrradiance: UnitDto? = null,
)

fun DetailDto.asDomainModel() = Detail(
    icon = icon,
    iconPhrase = iconPhrase,
    hasPrecipitation = hasPrecipitation,
    precipitationType = precipitationType,
    precipitationIntensity = precipitationIntensity,
    shortPhrase = shortPhrase,
    longPhrase = longPhrase,
    precipitationProbability = precipitationProbability,
    thunderstormProbability = thunderstormProbability,
    rainProbability = rainProbability,
    snowProbability = snowProbability,
    iceProbability = iceProbability,
    wind = wind?.asDomainModel(),
    windGust = windGust?.asDomainModel(),
    totalLiquid = totalLiquid?.asDomainModel(),
    rain = rain?.asDomainModel(),
    snow = snow?.asDomainModel(),
    ice = ice?.asDomainModel(),
    hoursOfPrecipitation = hoursOfPrecipitation,
    hoursOfRain = hoursOfRain,
    hoursOfSnow = hoursOfSnow,
    hoursOfIce = hoursOfIce,
    cloudCover = cloudCover,
    evapotranspiration = evapotranspiration?.asDomainModel(),
    solarIrradiance = solarIrradiance?.asDomainModel()
)

