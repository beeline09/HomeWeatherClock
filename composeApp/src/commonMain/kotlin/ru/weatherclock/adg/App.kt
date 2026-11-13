package ru.weatherclock.adg

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import ru.weatherclock.adg.app.presentation.screens.home.HomeScreen
import ru.weatherclock.adg.app.presentation.screens.settings.SettingsScreen
import ru.weatherclock.adg.theme.AppTheme

private var showToast: ((text: String, actionLabel: String, onActionClick: () -> Unit) -> Unit)? =
    null

fun showToast(
    text: String,
    actionLabel: String = "",
    onActionClick: () -> Unit = {}
) {
    showToast?.invoke(
        text,
        actionLabel,
        onActionClick
    )
}

@Composable
internal fun App(
    isDarkThemeSupported: Boolean,
    systemIsDark: Boolean = isSystemInDarkTheme(),
) = AppTheme(
    isDarkThemeSupported = isDarkThemeSupported,
    systemIsDark = systemIsDark
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    showToast = { text, action, onClick ->
        coroutineScope.launch {
            // Новый тост отменяет ещё показывающийся: без очереди из
            // устаревших сообщений (клика по дню месяца подряд и т.п.)
            snackbarHostState.currentSnackbarData?.dismiss()
            val snackbarResult = snackbarHostState.showSnackbar(
                message = text,
                actionLabel = action,
                duration = SnackbarDuration.Short
            )
            when (snackbarResult) {
                SnackbarResult.Dismissed -> {}
                SnackbarResult.ActionPerformed -> onClick()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().background(color = Color.Black),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {},
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().background(color = Color.Black)
        ) {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        onOpenSettings = { navController.navigate("settings") }
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        onBack = {
                            Logger.d("Pop settings")
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}