package ru.weatherclock.adg.app.data.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import ru.weatherclock.adg.app.data.dto.ColorTheme

object ColorThemeSerializer: KSerializer<ColorTheme> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "ColorTheme",
        PrimitiveKind.STRING
    )

    override fun serialize(
        encoder: Encoder,
        value: ColorTheme
    ) {
        val string = value.name
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): ColorTheme {
        val string = decoder.decodeString()
        return ColorTheme.valueOf(string)
    }
}