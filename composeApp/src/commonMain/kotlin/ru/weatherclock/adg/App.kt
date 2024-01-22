package ru.weatherclock.adg

import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import co.touchlab.kermit.Logger
import io.kamel.core.config.KamelConfig
import io.kamel.image.config.LocalKamelConfig
import ru.weatherclock.adg.app.domain.model.WeatherSettings
import ru.weatherclock.adg.app.domain.model.orDefault
import ru.weatherclock.adg.app.presentation.tabs.HomeTab
import ru.weatherclock.adg.app.presentation.tabs.SettingsTab
import ru.weatherclock.adg.platformSpecific.weatherSettingsKStore
import ru.weatherclock.adg.theme.AppTheme

private var showToast: ((text: String, actionLabel: String, onActionClick: () -> Unit) -> Unit)? =
    null

fun showToast(
    text: String, actionLabel: String = "", onActionClick: () -> Unit = {}
) {
    showToast?.invoke(
        text, actionLabel, onActionClick
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun App(isDarkThemeSupported: Boolean, kamelConfig: KamelConfig) =
    CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {
        AppTheme(isDarkThemeSupported = isDarkThemeSupported) {

            var isToolbarShowed by remember { mutableStateOf(false) }
            val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()
            val weatherSettings by weatherSettingsKStore.updates.collectAsState(WeatherSettings())
            val settings = weatherSettings.orDefault()
            val initialScreen = if (settings.weatherEnabled && settings.weatherKey.isBlank()) {
                SettingsTab
            } else {
                HomeTab
            }

            showToast = { text, action, onClick ->
                coroutineScope.launch { // using the `coroutineScope` to `launch` showing the snackbar
                    // taking the `snackbarHostState` from the attached `scaffoldState`
                    val snackbarResult: SnackbarResult =
                        scaffoldState.snackbarHostState.showSnackbar(
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
                topBar = {
                    if (isToolbarShowed) {
                        TopAppBar {
                            Text("Settingss")
                        }
                    }
                },
            ) {

                Box(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight()
                        .background(color = Color.Black)
//                        .windowInsetsPadding(WindowInsets.safeDrawing)
                ) {
                    Navigator(
                        screen = initialScreen,
                        onBackPressed = {
                            Logger.d("Pop screen #${(it as Tab).key}")
                            true
                        })/*        TabNavigator(HomeTab) {
                            BottomSheetNavigator(
                                modifier = Modifier.animateContentSize(),
                                sheetShape = RoundedCornerShape(
                                    topStart = 32.dp,
                                    topEnd = 32.dp
                                ),
                                skipHalfExpanded = true
                            ) {
                                Navigator(Application()) { navigator -> SlideTransition(navigator) }
                            }
                        }*/
                }
            }
        }
    }

internal expect fun openUrl(url: String?)