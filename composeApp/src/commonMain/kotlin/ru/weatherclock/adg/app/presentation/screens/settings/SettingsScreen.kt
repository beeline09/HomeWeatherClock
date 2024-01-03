package ru.weatherclock.adg.app.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
fun SettingsScreen() {
    val navigator = LocalNavigator.currentOrThrow
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "app bar title") },
                navigationIcon = if (navigator.size > 0) {
                    {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                } else {
                    null
                }

            )
        },
        content = {
            Box(modifier = Modifier.background(Color.Green).padding(top = 100.dp).fillMaxSize()) {
                Button(onClick = {}) {
                    Text(text = "Click me")
                }
//        BasisEpicCalendar(modifier = Modifier.background(Color.Green).fillMaxSize())
                /*        val dateTime = mutableStateOf(LocalDateTime.now())
                        val holder = mutableStateOf<DateInput>(DateInput.SingleDate())
                        Calendar(
                            dateTime = dateTime,
                            dateHolder = holder,
                            background = Color.White,
                            errorMessage = mutableStateOf(""),
                            locale = DateInputDefaults.DateInputLocale.RU,
                            onDateSelected = {},
                        )*/
            }
        }
    )
}