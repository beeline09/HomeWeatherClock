package ru.weatherclock.adg.app.presentation.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.presentation.components.calendar.color
import ru.weatherclock.adg.app.presentation.components.text.toMonthName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
fun TextCalendar(
    modifier: Modifier = Modifier,
    dayOfMonth: Int,
    month: Int,
    year: Int,
    prodCalendarDay: ProdCalendarDay?
) {
    val colorPalette = LocalCustomColorsPalette.current
    val dateStr = "$dayOfMonth".padStart(
        2, '0'
    )/* + "." + "$month".padStart(
        2,
        '0'
    )*/
    val dayColor = prodCalendarDay?.color() ?: colorPalette.dateDay
    Column(modifier = modifier.fillMaxSize()) {
        val tStyle = MaterialTheme.typography.bodySmall.copy(
            textAlign = TextAlign.Center
        )
        val tSize = TextAutoSize.StepBased(
            minFontSize = 5.sp, maxFontSize = TextAutoSizeDefaults.MaxFontSize, stepSize = 0.25.sp
        )

        BasicText(
            text = dateStr,
            maxLines = 1,
            autoSize = tSize,
            modifier = Modifier.fillMaxWidth().weight(0.25f)
                .wrapContentHeight(align = Alignment.CenterVertically),
            style = tStyle.copy(
                color = dayColor
            )
        )

        BasicText(
            text = month.toMonthName().replaceFirstChar { it.uppercaseChar() },
            maxLines = 1,
            autoSize = tSize,
            modifier = Modifier.fillMaxWidth().weight(0.15f)
                .wrapContentHeight(align = Alignment.CenterVertically),
            style = tStyle.copy(
                color = colorPalette.dateMonth,
            )
        )

        BasicText(
            text = "$year",
            maxLines = 1,
            autoSize = tSize,
            modifier = Modifier.fillMaxWidth().weight(0.25f)
                .wrapContentHeight(align = Alignment.CenterVertically),
            style = tStyle.copy(
                color = colorPalette.dateYear,
            )
        )
    }
}