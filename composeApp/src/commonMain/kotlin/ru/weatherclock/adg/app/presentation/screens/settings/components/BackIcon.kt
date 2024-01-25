package ru.weatherclock.adg.app.presentation.screens.settings.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
fun Navigator.backIcon(): @Composable (() -> Unit)? {
    val colorsPalette = LocalCustomColorsPalette.current
    return if (size > 1) {
        {
            IconButton(onClick = ::pop) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colorsPalette.background
                )
            }
        }
    } else {
        null
    }
}