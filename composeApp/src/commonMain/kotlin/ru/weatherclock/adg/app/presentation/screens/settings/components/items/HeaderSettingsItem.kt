package ru.weatherclock.adg.app.presentation.screens.settings.components.items

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.weatherclock.adg.app.domain.model.settings.SettingsHeader
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Suppress("UnusedReceiverParameter")
@Composable
fun LazyItemScope.HeaderSettingsItem(item: SettingsHeader) {
    val colorsPalette = LocalCustomColorsPalette.current
    val title = item.settingsKey.getName()
    Text(
        text = title,
        color = colorsPalette.clockText,
        fontSize = 20.sp,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 25.dp),
        fontWeight = FontWeight.Bold
    )
}