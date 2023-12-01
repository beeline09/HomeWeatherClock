package ru.weatherclock.adg.app.data.dto.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.weatherclock.adg.app.domain.model.forecast.AirAndPollen

@Serializable
data class AirAndPollenDto(

    /**
     * Название пыльцы или загрязнителя воздуха.
     */
    @SerialName("Name")
    val name: String = "",

    /**
     * Ценность загрязнителя. Значения, связанные с плесенью, травой, сорняками и деревом,
     *  отображаются в частях на кубический метр. Качество воздуха и УФ-индекс являются индексами
     *  и не имеют единиц измерения. Может быть НУЛЬ.
     */
    @SerialName("Value")
    val value: Int? = null,

    /**
     * Категория загрязнения. (низкий, высокий, хороший, средний, вредный, опасный)
     */
    @SerialName("Category")
    val category: String = "",

    /**
     * Значение, связанное с категорией. Эти значения варьируются от 1 до 6,
     * где 1 означает хорошие условия, а 6 — опасные условия.
     */
    @SerialName("CategoryValue")
    val categoryValue: Int = -1,

    /**
     * Существует только для качества воздуха. Примеры включают загрязнение озоном и частицами.
     */
    @SerialName("Type")
    val type: String = ""
)

fun AirAndPollenDto.asDomainModel(): AirAndPollen = AirAndPollen(
    name = name,
    value = value,
    category = category,
    categoryValue = categoryValue,
    type = type
)