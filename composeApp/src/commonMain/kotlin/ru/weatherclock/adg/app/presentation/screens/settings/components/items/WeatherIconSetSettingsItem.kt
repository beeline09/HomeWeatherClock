package ru.weatherclock.adg.app.presentation.screens.settings.components.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.setting_title_weather_icon_set
import homeweatherclock.composeapp.generated.resources.weather_icon_set_meteocons_fill
import homeweatherclock.composeapp.generated.resources.weather_icon_set_meteocons_line
import homeweatherclock.composeapp.generated.resources.weather_icon_set_official
import homeweatherclock.composeapp.generated.resources.weather_icon_set_twemoji
import homeweatherclock.composeapp.generated.resources.weather_icon_set_wi
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.app.data.dto.WeatherIconSet
import ru.weatherclock.adg.app.data.dto.WeatherServer
import ru.weatherclock.adg.app.domain.model.settings.WeatherIconSetSetting
import ru.weatherclock.adg.app.presentation.components.dialog.ClockAlertDialog
import ru.weatherclock.adg.app.presentation.components.weather.rememberWeatherIconRequestData
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getDescription
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Suppress("UnusedReceiverParameter")
@Composable
fun LazyItemScope.WeatherIconSetSettingsItem(item: WeatherIconSetSetting) {
    var showDialog by remember { mutableStateOf(false) }
    val description = item.settingsKey.getDescription()
    val alpha = if (item.isEnabled) 1f else ContentAlpha.disabled
    val colorsPalette = LocalCustomColorsPalette.current

    val titleStr = buildString {
        append(item.settingsKey.getName())
        append(": ")
        append(item.currentValue.getName())
    }

    if (showDialog && item.isEnabled) {
        WeatherIconSetDialog(
            currentSet = item.currentValue,
            server = item.currentServer,
            onSelected = {
                showDialog = false
                if (it != item.currentValue) item.onChange.invoke(it)
            },
            onDismiss = { showDialog = false }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .clickable(enabled = item.isEnabled) {
                showDialog = true
            },
    ) {
        Text(text = titleStr,
            color = colorsPalette.clockText,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .alpha(alpha = alpha)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .let {
                    if (description.isBlank()) {
                        it.padding(bottom = 16.dp)
                    } else it
                })
        if (description.isNotBlank()) {
            Text(
                text = description,
                fontSize = 13.sp,
                color = colorsPalette.clockText,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .alpha(alpha = alpha)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(
                        bottom = 16.dp,
                        top = 8.dp
                    )
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun WeatherIconSetDialog(
    currentSet: WeatherIconSet,
    server: WeatherServer,
    onSelected: (WeatherIconSet) -> Unit,
    onDismiss: () -> Unit
) {
    val colorsPalette = LocalCustomColorsPalette.current
    // Сэмпл-коды для превью: ясно / дождь / гроза (день)
    val sampleCodes = if (server == WeatherServer.OpenWeatherMap) {
        listOf("01d", "10d", "11d")
    } else {
        listOf("1", "12", "15")
    }

    ClockAlertDialog(
        title = SettingKeyTitle(),
        dismissRequest = onDismiss
    ) {
        LazyColumn {
            items(items = WeatherIconSet.entries.toList()) { set ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelected(set) }
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = set == currentSet,
                        onClick = { onSelected(set) }
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    sampleCodes.forEach { code ->
                        AsyncImage(
                            model = rememberWeatherIconRequestData(
                                code = code,
                                server = server,
                                iconSet = set
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(horizontal = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = set.getName(),
                        fontSize = 16.sp,
                        color = colorsPalette.clockText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingKeyTitle(): String =
    stringResource(Res.string.setting_title_weather_icon_set)

@Composable
private fun WeatherIconSet.getName(): String = stringResource(
    when (this) {
        WeatherIconSet.Official -> Res.string.weather_icon_set_official
        WeatherIconSet.MeteoconsFill -> Res.string.weather_icon_set_meteocons_fill
        WeatherIconSet.MeteoconsLine -> Res.string.weather_icon_set_meteocons_line
        WeatherIconSet.Twemoji -> Res.string.weather_icon_set_twemoji
        WeatherIconSet.WiMono -> Res.string.weather_icon_set_wi
    }
)
