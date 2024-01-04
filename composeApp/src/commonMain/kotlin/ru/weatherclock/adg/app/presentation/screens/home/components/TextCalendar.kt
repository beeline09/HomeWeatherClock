package ru.weatherclock.adg.app.presentation.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.presentation.components.calendar.color
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
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
        2,
        '0'
    )/* + "." + "$month".padStart(
        2,
        '0'
    )*/
    val dayColor = prodCalendarDay?.color() ?: colorPalette.dateDay
    Column(modifier = modifier.fillMaxSize()) {
        AutoSizeText(
            text = dateStr,
            color = dayColor,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
                .wrapContentHeight(align = Alignment.CenterVertically),
            alignment = Alignment.Center,
            style = MaterialTheme.typography.bodySmall,
        )
        AutoSizeText(
            text = month.toMonthName().replaceFirstChar { it.uppercaseChar() },
            color = colorPalette.dateMonth,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15f)
                .wrapContentHeight(align = Alignment.CenterVertically),
            alignment = Alignment.Center,
            style = MaterialTheme.typography.bodySmall,
        )
        AutoSizeText(
            text = "$year",
            color = colorPalette.dateYear,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
                .wrapContentHeight(align = Alignment.CenterVertically),
            alignment = Alignment.Center,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}