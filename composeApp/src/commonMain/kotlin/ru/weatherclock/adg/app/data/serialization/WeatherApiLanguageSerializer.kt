package ru.weatherclock.adg.app.data.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import ru.weatherclock.adg.app.data.dto.WeatherApiLanguage

object WeatherApiLanguageSerializer: KSerializer<WeatherApiLanguage> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "WeatherApiLanguage",
        PrimitiveKind.STRING
    )

    override fun serialize(
        encoder: Encoder,
        value: WeatherApiLanguage
    ) {
        val string = value.name
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): WeatherApiLanguage {
        val string = decoder.decodeString()
        return WeatherApiLanguage.valueOf(string)
    }
}