package ru.weatherclock.adg.app.presentation.screens.settings.components.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.domain.model.settings.HoursRangeSetting
import ru.weatherclock.adg.app.presentation.components.radiobutton.RadioGroupDialog
import ru.weatherclock.adg.app.presentation.components.util.padStart
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getDescription
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Suppress("UnusedReceiverParameter")
@Composable
fun LazyItemScope.HoursSettingsItem(item: HoursRangeSetting) {
    val title = item.settingsKey.getName()
    val description = item.settingsKey.getDescription()
    val alpha = if (item.isEnabled) 1f else ContentAlpha.disabled
    val colorsPalette = LocalCustomColorsPalette.current
    val startHourStr = item.currentValue.first.padStart(2)
    val endHourStr = item.currentValue.second.padStart(2)

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Text(text = title,
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
        Row(
            modifier = Modifier
                .wrapContentHeight(align = Alignment.CenterVertically)
                .padding(all = 16.dp)
        ) {
            var showStartHour by remember { mutableStateOf(false) }
            var showEndHour by remember { mutableStateOf(false) }
            val converter: (Int) -> String = { "${it.padStart(2)}:00" }
            val hoursRange: List<Int> = (0..23).toList()
            if (showStartHour && item.isEnabled) {
                hoursRange.RadioGroupDialog(title = stringResource(MR.strings.setting_hours_dialog_title_start),
                    converter = converter,
                    dismissRequest = {
                        showStartHour = false
                        it?.let { selectedHour ->
                            val endHour = item.currentValue.second
                            item.onChange.invoke(selectedHour to endHour)
                        }
                    },
                    isChecked = { item.currentValue.first == it },
                    isEnabled = { item.currentValue.second != it })
            }
            if (showEndHour && item.isEnabled) {
                hoursRange.RadioGroupDialog(title = stringResource(MR.strings.setting_hours_dialog_title_end),
                    converter = converter,
                    dismissRequest = {
                        showEndHour = false
                        it?.let { selectedHour ->
                            val startHour = item.currentValue.first
                            item.onChange.invoke(startHour to selectedHour)
                        }
                    },
                    isChecked = { item.currentValue.second == it },
                    isEnabled = { item.currentValue.first != it })
            }
            HourButton(
                item = item,
                hourValue = startHourStr,
                hint = MR.strings.setting_hours_item_start_hour
            ) {
                showStartHour = true
            }
            HourButton(
                item = item,
                hourValue = endHourStr,
                hint = MR.strings.setting_hours_item_end_hour
            ) {
                showEndHour = true
            }
        }

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
private fun RowScope.HourButton(
    item: HoursRangeSetting,
    hourValue: String,
    hint: StringResource,
    onClick: () -> Unit,
) {
    val alpha = if (item.isEnabled) 1f else ContentAlpha.disabled

    Button(
        enabled = item.isEnabled,
        modifier = Modifier.fillMaxWidth().weight(1f).padding(
            start = 16.dp,
            end = 16.dp
        ).wrapContentHeight(align = Alignment.CenterVertically),
        onClick = onClick
    ) {
        Column {
            Text(
                text = "${hourValue}:00",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .alpha(alpha = alpha)
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            Text(
                text = stringResource(hint),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .alpha(alpha = alpha)
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }
    }
}