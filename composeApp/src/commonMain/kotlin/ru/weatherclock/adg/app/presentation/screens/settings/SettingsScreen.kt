package ru.weatherclock.adg.app.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.domain.model.WeatherSettings
import ru.weatherclock.adg.app.presentation.screens.settings.component.backIcon

@Composable
fun SettingsScreen(screenModel: SettingsScreenViewModel = koinInject()) {
    val navigator = LocalNavigator.currentOrThrow
    val state by screenModel.state.collectAsState(SettingsScreenState(WeatherSettings()))
    val settings = state.settings
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = stringResource(MR.strings.settings_toolbar)) },
            navigationIcon = navigator.backIcon()
        )
    },
        content = {
            Box(modifier = Modifier.background(Color.Green).padding(top = 100.dp).fillMaxSize()) {
                Button(onClick = {}) {
                    Text(text = "Click me")
                }
            }
        })
}