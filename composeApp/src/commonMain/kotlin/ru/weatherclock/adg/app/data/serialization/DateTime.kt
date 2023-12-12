package ru.weatherclock.adg.app.data.serialization

import kotlinx.datetime.LocalDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object RussianDateSerializer: KSerializer<LocalDate> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            "Instant",
            PrimitiveKind.STRING
        )

    override fun deserialize(decoder: Decoder): LocalDate {
        val str = decoder.decodeString()
        val day = str.substring(
            0,
            2
        ).toIntOrNull() ?: 0
        val month = str.substring(
            3,
            5
        ).toIntOrNull() ?: 0
        val year = str.substring(
            6,
            10
        ).toIntOrNull() ?: 0
        return LocalDate(
            year = year,
            monthNumber = month,
            dayOfMonth = day
        )
    }

    override fun serialize(
        encoder: Encoder,
        value: LocalDate
    ) {
        encoder.encodeString(value.toString())
    }

}