package ru.weatherclock.adg

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import co.touchlab.kermit.Logger
import io.kamel.core.config.KamelConfig
import io.kamel.image.config.LocalKamelConfig
import kotlinx.coroutines.launch
import ru.weatherclock.adg.app.presentation.tabs.HomeTab
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
    kamelConfig: KamelConfig
) = CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {
    AppTheme(
        isDarkThemeSupported = isDarkThemeSupported,
        systemIsDark = systemIsDark
    ) {

        val scaffoldState = rememberScaffoldState()
        val coroutineScope = rememberCoroutineScope()

        showToast = { text, action, onClick ->
            coroutineScope.launch {
                val snackbarResult: SnackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                    message = text,
                    actionLabel = action
                )
                when (snackbarResult) {
                    SnackbarResult.Dismissed -> {}
                    SnackbarResult.ActionPerformed -> onClick()
                }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().background(color = Color.Black),
            scaffoldState = scaffoldState,
            topBar = {},
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight().background(color = Color.Black)
            ) {
                Navigator(screen = HomeTab,
                    onBackPressed = {
                        Logger.d("Pop screen #${(it as Tab).key}")
                        true
                    })
            }
        }
    }
}