package ru.weatherclock.adg.app.presentation.screens.settings.component

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun Navigator.backIcon(): @Composable (() -> Unit)? {
    return if (level > 0) {
        {
            IconButton(onClick = { pop() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    } else {
        null
    }
}