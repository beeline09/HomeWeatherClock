package ru.weatherclock.adg.app.data.dto.forecast.accuweather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccuweatherAutoCompleteDto(

    @SerialName("Version")
    val version: Int = 0,
    @SerialName("Key")
    val key: String,
    @SerialName("Type")
    val typeDto: TypeDto = TypeDto.Unknown,
    @SerialName("Rank")
    val rank: Int = 0,
    @SerialName("LocalizedName")
    val localizedName: String = "",
    @SerialName("Country")
    val country: CountryDto = CountryDto(),
    @SerialName("AdministrativeArea")
    val administrativeArea: AdministrativeAreaDto = AdministrativeAreaDto()
)

@Serializable
data class AdministrativeAreaDto(
    @SerialName("ID")
    val id: String = "",
    @SerialName("LocalizedName")
    val localizedName: String = "",
)

@Serializable
data class CountryDto(
    @SerialName("ID")
    val id: String = "",
    @SerialName("LocalizedName")
    val localizedName: String = ""
)

@Serializable
enum class TypeDto {

    @SerialName("City")
    City,

    @SerialName("PostalCode")
    PostalCode,

    @SerialName("POI")
    POI,

    @SerialName("LatLong")
    LatLong,
    Unknown
}
