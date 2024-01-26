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
import ru.weatherclock.adg.app.domain.model.russiaRegions
import ru.weatherclock.adg.app.domain.model.settings.IntSetting
import ru.weatherclock.adg.app.domain.model.settings.SettingKey
import ru.weatherclock.adg.app.domain.model.toRegion
import ru.weatherclock.adg.app.presentation.components.radiobutton.RadioGroupDialog
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getDescription
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Suppress("UnusedReceiverParameter")
@Composable
fun LazyItemScope.IntSettingsItem(item: IntSetting) {
    var showRegionsDialog by remember { mutableStateOf(false) }
    val description = item.settingsKey.getDescription()
    val alpha = if (item.isEnabled) 1f else ContentAlpha.disabled
    val colorsPalette = LocalCustomColorsPalette.current
    val allRegionsStr = stringResource(MR.strings.setting_prod_calendar_region_all_russia)
    val title = item.settingsKey.getName()
    val titleStr = buildString {
        append(title)
        when (item.settingsKey) {
            SettingKey.ProdCalendarRussiaRegion -> {
                append(": ")
                if (item.currentValue <= 0) {
                    append(allRegionsStr)
                } else {
                    val regionMap = russiaRegions.entries.firstOrNull {
                        it.value.contains(item.currentValue.toRegion())
                    }
                    append(regionMap?.key.orEmpty())
                }
            }

            else -> error("Пока не реализовано...")
        }
    }

    if (showRegionsDialog) {
        russiaRegions.keys
            .toList()
            .RadioGroupDialog(title = stringResource(MR.strings.setting_prod_calendar_dialog_select_region_title),
                converter = {
                    val numbers = russiaRegions[it]?.sorted()?.joinToString(separator = ", ")
                    if (numbers?.toIntOrNull() == 0) allRegionsStr
                    else buildString {
                        append(it)
                        if (!numbers.isNullOrBlank()) {
                            append(" (")
                            append(numbers)
                            append(")")
                        }
                    }
                },
                dismissRequest = {
                    showRegionsDialog = false
                    val selectedRegionNumber =
                        russiaRegions[it]?.sorted()?.firstOrNull()?.toIntOrNull() ?: 0
                    item.onChange.invoke(selectedRegionNumber)
                },
                isEnabled = item.isEnabled,
                isChecked = {
                    russiaRegions[it]?.contains(item.currentValue.toRegion()) == true
                })
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .clickable(enabled = item.isEnabled) {
                when (item.settingsKey) {
                    SettingKey.ProdCalendarRussiaRegion -> {
                        showRegionsDialog = true
                    }

                    else -> error("Пока не реализовано...")
                }
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