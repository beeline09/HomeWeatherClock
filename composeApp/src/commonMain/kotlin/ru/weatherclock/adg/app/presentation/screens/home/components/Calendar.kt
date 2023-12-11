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
fun calendar(
    modifier: Modifier = Modifier,
    dayOfMonth: Int,
    month: String,
    year: Int
) {
    Column(modifier = modifier.fillMaxSize()) {
        AutoSizeText(
            text = "$dayOfMonth".padStart(
                2,
                '0'
            ),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .background(Color.Cyan),
            alignment = Alignment.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
        AutoSizeText(
            text = month,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.15f)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .background(Color.Gray),
            alignment = Alignment.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
        AutoSizeText(
            text = "$year",
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.25f)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .background(Color.Cyan),
            alignment = Alignment.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}