package ru.weatherclock.adg.app.presentation.components.radiobutton

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
inline fun <T> radioButton(
    isEnabled: Boolean,
    isChecked: Boolean,
    item: T,
    converter: (T) -> String,
    noinline onCheckedChange: (T) -> Unit
) {
    val colorsPalette = LocalCustomColorsPalette.current
    val alpha = if (isEnabled) 1f else ContentAlpha.disabled
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp).clickable(enabled = isEnabled) {
            onCheckedChange.invoke(item)
        }) {
        RadioButton(
            selected = isChecked,
            onClick = {
                onCheckedChange.invoke(item)
            },
            enabled = isEnabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = colorsPalette.switchChecked,
                unselectedColor = colorsPalette.switchUnchecked,
                disabledColor = colorsPalette.switchDisabled
            )
        )
        Text(
            text = converter.invoke(item),
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