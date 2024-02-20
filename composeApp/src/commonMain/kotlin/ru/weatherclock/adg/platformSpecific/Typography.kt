package ru.weatherclock.adg.platformSpecific

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.ds_digi_bold
import homeweatherclock.composeapp.generated.resources.ds_digi_italic
import homeweatherclock.composeapp.generated.resources.ds_digi_italic_bold
import homeweatherclock.composeapp.generated.resources.ds_digi_regular
import org.jetbrains.compose.resources.Font

@Composable
fun getTypography(): Typography {

    val dsDigiRegular = FontFamily(Font(Res.font.ds_digi_regular))

    val dsDigiItalic = FontFamily(Font(Res.font.ds_digi_italic))

    val dsDigiItalicBold = FontFamily(Font(Res.font.ds_digi_italic_bold))
    val dsDigiBold = FontFamily(Font(Res.font.ds_digi_bold))
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