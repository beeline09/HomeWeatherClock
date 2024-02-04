package ru.weatherclock.adg.app.data.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import ru.weatherclock.adg.app.data.WeatherUnits

object WeatherUnitsSerializer: KSerializer<WeatherUnits> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "WeatherUnits",
        PrimitiveKind.STRING
    )

    override fun serialize(
        encoder: Encoder,
        value: WeatherUnits
    ) {
        val string = when (value) {
            WeatherUnits.Standard -> "standard"
            WeatherUnits.Imperial -> "imperial"
            WeatherUnits.Metric -> "metric"
        }
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): WeatherUnits {
        return when (decoder.decodeString()) {
            "standard" -> WeatherUnits.Standard
            "imperial" -> WeatherUnits.Imperial
            else -> WeatherUnits.Metric
        }
    }
}