package ru.weatherclock.adg.app.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
import ru.weatherclock.adg.app.presentation.components.text.toMonthName

@Composable
fun TextCalendar(
    modifier: Modifier = Modifier,
    dayOfMonth: Int,
    month: Int,
    year: Int,
    dayName: String
) {
    val dateStr = "$dayOfMonth".padStart(
        2,
        '0'
    )/* + "." + "$month".padStart(
        2,
        '0'
    )*/
    Column(modifier = modifier.fillMaxSize()) {
        AutoSizeText(
            text = dateStr,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .background(Color.Cyan),
            alignment = Alignment.Center,
            style = MaterialTheme.typography.bodySmall,
        )
        AutoSizeText(
            text = month.toMonthName(),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15f)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .background(Color.Gray),
            alignment = Alignment.Center,
            style = MaterialTheme.typography.bodySmall,
        )
        AutoSizeText(
            text = "$year",
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .background(Color.Yellow),
            alignment = Alignment.Center,
            style = MaterialTheme.typography.bodySmall,
        )
        /*      AutoSizeText(
                  text = dayName,
                  maxLines = 1,
                  modifier = Modifier
                      .fillMaxWidth()
                      .weight(0.15f)
                      .wrapContentHeight(align = Alignment.CenterVertically)
                      .background(Color.Gray),
                  alignment = Alignment.Center,
                  style = MaterialTheme.typography.bodySmall,
              )*/
    }
}