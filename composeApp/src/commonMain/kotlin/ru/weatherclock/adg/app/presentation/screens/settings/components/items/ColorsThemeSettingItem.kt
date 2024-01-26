package ru.weatherclock.adg.app.presentation.screens.settings.components.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.ContentAlpha
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
fun ColorTheme.radioButton(item: ColorsThemeSetting) {
    ru.weatherclock.adg.app.presentation.components.radiobutton.radioButton(isChecked = item.currentValue == this@radioButton,
        isEnabled = item.isEnabled,
        item = item,
        converter = { getName() },
        onCheckedChange = {
            item.onChange.invoke(this)
        })
}