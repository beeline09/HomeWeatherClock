package ru.weatherclock.adg.app.presentation.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.koinInject

@Composable
fun HomeScreen(screenModel: HomeScreenViewModel = koinInject()) {
    val forecast by screenModel.forecast.collectAsState()
    LaunchedEffect(Unit) { screenModel.onLaunch() }

}