package ru.weatherclock.adg.platformSpecific

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.fontFamilyResource
import ru.weatherclock.adg.MR

@Composable
fun getTypography(): Typography {

    val dsDigiRegular = fontFamilyResource(MR.fonts.ds_digi_regular.ds_digi_regular)

    val dsDigiItalic = fontFamilyResource(MR.fonts.ds_digi_italic.ds_digi_italic)

    val dsDigiItalicBold = fontFamilyResource(MR.fonts.ds_digi_italic_bold.ds_digi_italic_bold)
    val dsDigiBold = fontFamilyResource(MR.fonts.ds_digi_bold.ds_digi_bold)
    return Typography(
        displayLarge = TextStyle(
            fontFamily = dsDigiItalicBold,
            fontSize = 400.sp
        ),
        displayMedium = TextStyle(
            fontFamily = dsDigiItalic,
            fontSize = 400.sp
        ),
        displaySmall = TextStyle(
            fontFamily = dsDigiBold,
            fontSize = 400.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = dsDigiRegular,
            fontSize = 400.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
    )
}