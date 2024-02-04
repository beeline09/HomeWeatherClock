package ru.weatherclock.adg.app.presentation.screens.settings.components.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.ContentAlpha
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
import dev.icerock.moko.resources.compose.stringResource
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.data.dto.WeatherApiLanguage
import ru.weatherclock.adg.app.domain.model.settings.WeatherApiLanguageSetting
import ru.weatherclock.adg.app.presentation.components.radiobutton.RadioGroupDialog
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getDescription
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Suppress("UnusedReceiverParameter")
@Composable
fun LazyItemScope.ApiLanguageSettingsItem(item: WeatherApiLanguageSetting) {
    var showDialog by remember { mutableStateOf(false) }
    val description = item.settingsKey.getDescription()
    val alpha = if (item.isEnabled) 1f else ContentAlpha.disabled
    val colorsPalette = LocalCustomColorsPalette.current

    val items = WeatherApiLanguage.entries.map {
        it to it.getName(withCode = it == WeatherApiLanguage.System)
    }

    val titleStr = buildString {
        append(item.settingsKey.getName())
        append(": ")
        append(item.currentValue.getName(withCode = false))
    }

    if (showDialog && item.isEnabled) {
        items.RadioGroupDialog(title = item.settingsKey.getName(),
            converter = { it.second },
            dismissRequest = {
                showDialog = false
                it?.let { pair ->
                    item.onChange.invoke(pair.first)
                }
            },
            isEnabled = { true },
            isChecked = {
                item.currentValue == it.first
            })
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
private fun WeatherApiLanguage.getName(withCode: Boolean): String = buildString {
    val name = when (this@getName) {
        WeatherApiLanguage.Russian -> stringResource(MR.strings.weather_api_language_ru)
        WeatherApiLanguage.English -> stringResource(MR.strings.weather_api_language_en)
        WeatherApiLanguage.System -> stringResource(MR.strings.weather_api_language_system)
    }
    append(name)
    if (withCode) {
        append(": ")
        append(code)
    }
}