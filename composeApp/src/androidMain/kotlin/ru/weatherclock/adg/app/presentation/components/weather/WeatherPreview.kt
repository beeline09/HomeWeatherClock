package ru.weatherclock.adg.app.presentation.components.weather

import kotlinx.datetime.LocalDate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.weatherclock.adg.app.data.dto.WeatherConfigData
import ru.weatherclock.adg.app.domain.model.forecast.DayDetail
import ru.weatherclock.adg.app.domain.model.forecast.ForecastDay
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.theme.AppTheme

@Composable
@Preview(
    widthDp = 220,
    heightDp = 150,
    locale = "ru"
)
fun WeatherCellPreview() {
    AppTheme(true) {
        Column(
            modifier = Modifier
//                .background(color = Color.Green)
                .fillMaxSize()
        ) {
            WeatherCell(
                isPreview = true,
                forecast = ForecastDay(
                    date = LocalDate.now(),
                    min = DayDetail(
                        temperature = -16.5,
                        icon = "10n",
                        iconPhrase = "Холодно пиздец"
                    ),
                    max = DayDetail(
                        temperature = 1.2,
                        icon = "10d",
                        iconPhrase = "Пойдёт"
                    )
                ),
                weatherConfigData = WeatherConfigData.Accuweather()
            )
        }
    }
}