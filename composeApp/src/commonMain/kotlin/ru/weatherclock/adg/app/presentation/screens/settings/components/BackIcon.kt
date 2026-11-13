package ru.weatherclock.adg.app.presentation.screens.settings.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
fun backIcon(onBack: () -> Unit): @Composable (() -> Unit) {
    val colorsPalette = LocalCustomColorsPalette.current
    return {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colorsPalette.background
            )
        }
    }
}
