package ru.weatherclock.adg.app.domain.model.forecast

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
)

