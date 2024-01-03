package ru.weatherclock.adg.app.presentation.components.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import ru.homeweatherclock.adg.MR
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
fun weekHeader() {
    val colorPalette = LocalCustomColorsPalette.current
    Row(
        Modifier
            .fillMaxWidth().wrapContentHeight()
    ) {
        (0..6).toList().forEach { weekNumber ->
            if (weekNumber == 0) {
                Spacer(Modifier.width(4.dp))
            }
            AutoSizeText(
                color = if (weekNumber < 5) colorPalette.calendarWeekdayWorkText else colorPalette.calendarWeekdayWeekendText,
                text = weekNumber.toWeekName(),
                minTextSize = 1.sp,
                maxTextSize = 50.sp,
                stepGranularityTextSize = 1.sp,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(percent = 5))
//                    .getPointerCursor()
                    .border(
                        1.dp,
                        colorPalette.calendarWeekdayBorder,
                        RoundedCornerShape(percent = 5)
                    ).padding(3.dp).clickable(false) {},
                alignment = Alignment.Center
            )
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
fun Int.toWeekName(): String = when (this) {
    0 -> stringResource(MR.strings.day_name_short_monday)
    1 -> stringResource(MR.strings.day_name_short_tuesday)
    2 -> stringResource(MR.strings.day_name_short_wednesday)
    3 -> stringResource(MR.strings.day_name_short_thursday)
    4 -> stringResource(MR.strings.day_name_short_friday)
    5 -> stringResource(MR.strings.day_name_short_saturday)
    6 -> stringResource(MR.strings.day_name_short_sunday)
    else -> ""
}