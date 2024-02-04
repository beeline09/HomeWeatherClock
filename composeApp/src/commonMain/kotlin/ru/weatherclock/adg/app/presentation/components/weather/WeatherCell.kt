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
import ru.weatherclock.adg.app.data.dto.WeatherConfigData
import ru.weatherclock.adg.app.domain.model.forecast.ForecastDay
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
import ru.weatherclock.adg.app.presentation.components.text.formatForWeatherCell
import ru.weatherclock.adg.app.presentation.components.text.toTemperature
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
fun ColumnScope.WeatherCell(
    forecast: ForecastDay,
    weatherConfigData: WeatherConfigData,
    isPreview: Boolean = false
) {
    val colorPalette = LocalCustomColorsPalette.current
    //Температура
    Row(
        modifier = Modifier
            .fillMaxSize()
            .weight(3.5f)
            .padding(horizontal = 5.dp)
//            .wrapContentHeight(align = Alignment.CenterVertically)
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
                text = forecast.min.temperature.toTemperature(unitType = weatherConfigData.units),
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
                color = colorPalette.weatherSeverityOther,
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
                text = forecast.max.temperature.toTemperature(unitType = weatherConfigData.units),
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
                color = colorPalette.weatherSeverityOther
            )
        }
    }
    Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = colorPalette.divider))
    //Иконки погоды для дня и ночи
    Row(
        modifier = Modifier
            .fillMaxSize()
            .weight(4f)
            .padding(horizontal = 5.dp)
            .padding(top = 2.dp)
//            .wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeatherIcon(
                detail = forecast.min,
                weatherConfigData = weatherConfigData,
                isPreview = isPreview
            )
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
            WeatherIcon(
                detail = forecast.max,
                weatherConfigData = weatherConfigData,
                isPreview = isPreview
            )
        }
    }
    //Краткое описание иконки погоды
    Row(
        modifier = Modifier
            .fillMaxSize()
            .weight(1.3f)
            .padding(horizontal = 5.dp)
            .padding(top = 2.dp)
//            .wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .align(Alignment.CenterVertically)
                .weight(1f)
        ) {
            AutoSizeText(
                text = forecast.min.iconPhrase.orEmpty(),
                maxTextSize = 11.sp,
                minTextSize = 5.sp,
                stepGranularityTextSize = 1.sp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 2.dp)
                    .align(Alignment.CenterHorizontally),
                alignment = Alignment.Center,
                maxLines = 1,
                color = colorPalette.weatherSeverityOther
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .weight(1f),
        ) {
            AutoSizeText(
                text = forecast.max.iconPhrase.orEmpty(),
                maxTextSize = 11.sp,
                minTextSize = 5.sp,
                stepGranularityTextSize = 1.sp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 2.dp)
                    .align(Alignment.CenterHorizontally),
                alignment = Alignment.Center,
                maxLines = 1,
                color = colorPalette.weatherSeverityOther
            )
        }
    }
    //Вертикальный разделитель над нижним текстом
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = colorPalette.divider)
    )
    //Нижний текст с датой
    Text(
        text = forecast.date.formatForWeatherCell(),
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 5.dp,
                vertical = 1.dp
            )
            .wrapContentHeight(align = Alignment.CenterVertically),
        textAlign = TextAlign.Center,
        maxLines = 1,
        style = MaterialTheme.typography.bodySmall,
        color = colorPalette.weatherSeverityOther
    )
}