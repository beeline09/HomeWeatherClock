package ru.weatherclock.adg.app.data.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import ru.weatherclock.adg.app.data.dto.forecast.openweathermap.detail.PartOfDay

object PartOfDaySerializer: KSerializer<PartOfDay> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "PartOfDay",
        PrimitiveKind.STRING
    )

    override fun serialize(
        encoder: Encoder,
        value: PartOfDay
    ) {
        val string = when (value) {
            PartOfDay.Day -> "d"
            PartOfDay.Night -> "n"
        }
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): PartOfDay {
        return when (decoder.decodeString()) {
            "d" -> PartOfDay.Day
            else -> PartOfDay.Night
        }
    }
}