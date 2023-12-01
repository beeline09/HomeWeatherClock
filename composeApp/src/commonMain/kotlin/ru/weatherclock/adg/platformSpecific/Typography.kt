package ru.weatherclock.adg.platformSpecific

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
expect fun font(
    name: String,
    res: String,
    weight: FontWeight,
    style: FontStyle
): Font

@Composable
fun getTypography(): Typography {

    val dsDigiRegular = FontFamily(
        font(
            "DsDigi",
            "ds_digi_regular",
            FontWeight.Normal,
            FontStyle.Normal
        )
    )

    val dsDigiItalic = FontFamily(
        font(
            "DsDigi",
            "ds_digi_italic",
            FontWeight.Normal,
            FontStyle.Italic
        )
    )

    val dsDigiItalicBold = FontFamily(
        font(
            "DsDigi",
            "ds_digi_italic_bold",
            FontWeight.Bold,
            FontStyle.Italic
        )
    )
    val dsDigiBold = FontFamily(
        font(
            "DsDigi",
            "ds_digi_bold",
            FontWeight.Bold,
            FontStyle.Normal
        )
    )
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