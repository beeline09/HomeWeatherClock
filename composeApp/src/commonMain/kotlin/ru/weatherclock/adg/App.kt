package ru.weatherclock.adg

import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import co.touchlab.kermit.Logger
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun App() = AppTheme {

    var isToolbarShowed by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    showToast = { text, action, onClick ->
        coroutineScope.launch { // using the `coroutineScope` to `launch` showing the snackbar
            // taking the `snackbarHostState` from the attached `scaffoldState`
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
        modifier = Modifier,
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
            modifier = Modifier.background(Color.Black).fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Navigator(
                screen = HomeTab,
                onBackPressed = {
                    Logger.d("Pop screen #${(it as Tab).key}")
                    true
                }
            )
            /*        TabNavigator(HomeTab) {
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

class Application: Screen {

    private val bottomNavigationHeight = 56.dp + 12.dp

    @Composable
    override fun Content() {

        Scaffold(
            modifier = Modifier,
            /*            scaffoldState = scaffoldState,
                        topBar = {
                            if (isToolbarShowed) {
                                TopAppBar {
                                    Text("Settingss")
                                }
                            }
                        },*/
            bottomBar = {
                Card(
                    shape = RoundedCornerShape(50.dp),
                    elevation = 4.dp,
                    modifier = Modifier.padding(12.dp),
                ) {
                    BottomNavigation(
                        modifier = Modifier,
                        contentColor = Color.Green,
                        elevation = 4.dp,
                    ) {
                        /*TabNavigationItem(tab = HomeTab){
                            isToolbarShowed = false
                        }
                        TabNavigationItem(tab = SettingsTab){
                            isToolbarShowed = true
                        }*/
                    }
                }

            },
        ) {
            Row(Modifier.fillMaxSize()/*.padding(bottom = bottomNavigationHeight)*/) {
                CurrentTab()
            }
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(
    tab: Tab,
    onClick: (tab: Tab) -> Unit
) {
    val tabNavigator = LocalTabNavigator.current
    val title = tab.options.title
    BottomNavigationItem(
        modifier = Modifier,
        unselectedContentColor = Color.LightGray,
        selectedContentColor = Color.White,
        alwaysShowLabel = true,
        label = {
            Text(
                text = title,
            )
        },
        selected = tabNavigator.current.key == tab.key,
        onClick = {
            tabNavigator.current = tab
            onClick.invoke(tab)
        },
        icon = {
            Icon(
                painter = tab.options.icon!!,
                contentDescription = tab.options.title
            )
        })
}

internal expect fun openUrl(url: String?)