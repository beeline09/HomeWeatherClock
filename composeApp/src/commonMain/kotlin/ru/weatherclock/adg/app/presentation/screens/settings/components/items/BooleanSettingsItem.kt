package ru.weatherclock.adg.app.presentation.screens.settings.components.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.weatherclock.adg.app.domain.model.settings.BooleanSetting
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getDescription
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Suppress("UnusedReceiverParameter")
@Composable
fun LazyItemScope.BooleanSettingsItem(item: BooleanSetting) {
    val title = item.settingsKey.getName()
    val description = item.settingsKey.getDescription()
    val alpha = if (item.isEnabled) 1f else ContentAlpha.disabled
    val colorsPalette = LocalCustomColorsPalette.current
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .clickable(enabled = item.isEnabled) {
                item.onChange.invoke(!item.currentValue)
            }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .wrapContentHeight(align = Alignment.CenterVertically)
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
        Switch(
            checked = item.currentValue,
            enabled = item.isEnabled,
            onCheckedChange = item.onChange,
            modifier = Modifier.wrapContentSize(align = Alignment.Center)
        )
    }
}