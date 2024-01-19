package ru.weatherclock.adg.app.presentation.components.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.weatherclock.adg.app.domain.model.forecast.DailyForecast
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
import ru.weatherclock.adg.app.presentation.components.text.formatForWeatherCell
import ru.weatherclock.adg.app.presentation.components.text.toTemperature
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
fun ColumnScope.WeatherCell(forecast: DailyForecast) {
    val colorPalette = LocalCustomColorsPalette.current
    Row(
        modifier = Modifier
            .fillMaxSize()
            .weight(0.7f)
            .padding(horizontal = 5.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 2.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .align(Alignment.CenterVertically)
                .weight(1f)
        ) {
            AutoSizeText(
                text = forecast.temperature?.maximum?.let {
                    it.value.toTemperature(unit = it.unit)
                }.orEmpty(),
                maxTextSize = 85.sp,
                minTextSize = 5.sp,
                stepGranularityTextSize = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 5.dp,
                        end = 5.dp
                    )
                    .weight(1f)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                alignment = Alignment.Center,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(colorPalette.divider)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .weight(1f)
        ) {
            AutoSizeText(
                text = forecast.temperature?.minimum?.let {
                    it.value.toTemperature(unit = it.unit)
                }.orEmpty(),
                maxTextSize = 85.sp,
                minTextSize = 5.sp,
                stepGranularityTextSize = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 5.dp,
                        end = 5.dp
                    )
                    .weight(1f)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                alignment = Alignment.Center,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
    Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = colorPalette.divider))
    Row(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .padding(horizontal = 5.dp)
            .padding(top = 2.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            forecast.day?.let { WeatherIcon(it) }
        }
        Spacer(
            modifier = Modifier
                .width(10.dp)
                .fillMaxHeight()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            forecast.night?.let { WeatherIcon(it) }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .padding(top = 2.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .align(Alignment.CenterVertically)
                .weight(1f)
        ) {
            /*Text(
                text = forecast.day?.iconPhrase.orEmpty(),
                fontSize = 11.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .padding(top = 2.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
            )*/
            AutoSizeText(
                text = forecast.day?.iconPhrase.orEmpty(),
                maxTextSize = 11.sp,
                minTextSize = 4.sp,
                stepGranularityTextSize = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 5.dp,
                        vertical = 2.dp
                    )
                    .wrapContentHeight(align = Alignment.CenterVertically),
                alignment = Alignment.Center,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.width(1.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .weight(1f),
        ) {
            AutoSizeText(
                text = forecast.night?.iconPhrase.orEmpty(),
                maxTextSize = 11.sp,
                minTextSize = 4.sp,
                stepGranularityTextSize = 1.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 5.dp,
                        vertical = 2.dp
                    )
                    .wrapContentHeight(align = Alignment.CenterVertically),
                alignment = Alignment.Center,
                maxLines = 1
            )
        }
    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = colorPalette.divider)
    )
    Text(
        text = forecast.date.formatForWeatherCell(),
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 5.dp,
                vertical = 2.dp
            )
            .wrapContentHeight(align = Alignment.CenterVertically),
        textAlign = TextAlign.Center,
        maxLines = 1,
        style = MaterialTheme.typography.bodySmall,
    )
}