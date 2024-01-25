package ru.weatherclock.adg.app.presentation.screens.settings.components.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.ContentAlpha
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.weatherclock.adg.app.domain.model.settings.ColorTheme
import ru.weatherclock.adg.app.domain.model.settings.ColorsThemeSetting
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Suppress("UnusedReceiverParameter")
@Composable
fun LazyItemScope.ColorsThemeSettingItem(item: ColorsThemeSetting) {
    val title = item.settingsKey.getName()
    val alpha = if (item.isEnabled) 1f else ContentAlpha.disabled
    val colorsPalette = LocalCustomColorsPalette.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Text(
            text = title,
            color = colorsPalette.clockText,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .alpha(alpha = alpha)
                .fillMaxWidth()
                .padding(all = 16.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
        ColorTheme.entries.forEach {
            it.radioButton(item)
        }
    }
}

@Composable
private fun ColorTheme.radioButton(item: ColorsThemeSetting) {
    val colorsPalette = LocalCustomColorsPalette.current
    val alpha = if (item.isEnabled) 1f else ContentAlpha.disabled
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp).clickable(enabled = item.isEnabled) {
            item.onChange.invoke(this@radioButton)
        }) {
        RadioButton(
            selected = item.currentValue == this@radioButton,
            onClick = {
                item.onChange.invoke(this@radioButton)
            },
            enabled = item.isEnabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = colorsPalette.switchChecked,
                unselectedColor = colorsPalette.switchUnchecked,
                disabledColor = colorsPalette.switchDisabled
            )
        )
        Text(
            text = this@radioButton.getName(),
            color = colorsPalette.clockText,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .alpha(alpha = alpha)
                .fillMaxWidth()
                .padding(all = 16.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
    }
}