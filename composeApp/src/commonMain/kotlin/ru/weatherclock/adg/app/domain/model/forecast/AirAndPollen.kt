package ru.weatherclock.adg.app.domain.model.forecast

data class AirAndPollen(

    /**
     * Название пыльцы или загрязнителя воздуха.
     */
    val name: String,

    /**
     * Ценность загрязнителя. Значения, связанные с плесенью, травой, сорняками и деревом,
     *  отображаются в частях на кубический метр. Качество воздуха и УФ-индекс являются индексами
     *  и не имеют единиц измерения. Может быть НУЛЬ.
     */
    val value: Int? = null,

    /**
     * Категория загрязнения. (низкий, высокий, хороший, средний, вредный, опасный)
     */
    val category: String,

    /**
     * Значение, связанное с категорией. Эти значения варьируются от 1 до 6,
     * где 1 означает хорошие условия, а 6 — опасные условия.
     */
    val categoryValue: Int,

    /**
     * Существует только для качества воздуха. Примеры включают загрязнение озоном и частицами.
     */
    val type: String
)

fun AirAndPollen.asDbModel(forecastPid: Long): ru.weatherclock.adg.db.Accuweather.AirAndPollen {
    return ru.weatherclock.adg.db.Accuweather.AirAndPollen(
        name = name,
        value_ = value ?: -1,
        category = category,
        categoryValue = categoryValue,
        type = type,
        forecast_pid = forecastPid,
        pid = -1L
    )
}

fun ru.weatherclock.adg.db.Accuweather.AirAndPollen.asDomainModel(): AirAndPollen {
    return AirAndPollen(
        name = name,
        value = value_,
        category = category.orEmpty(),
        categoryValue = categoryValue,
        type = type.orEmpty(),
    )
}