package ru.weatherclock.adg

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import ru.weatherclock.adg.app.presentation.tabs.HomeTab
import ru.weatherclock.adg.theme.AppTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun App() = AppTheme {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.background(Color.Black).fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        TabNavigator(HomeTab) {
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
        }
    }
}

class Application: Screen {

    @Composable
    override fun Content() {

        Scaffold(
            modifier = Modifier,
            scaffoldState = rememberScaffoldState(),
            bottomBar = {
                Card(
                    shape = RoundedCornerShape(50.dp),
                    elevation = 4.dp,
                    modifier = Modifier.padding(12.dp)
                ) {
                    BottomNavigation(
                        modifier = Modifier,
                        contentColor = Color.Green,
                        elevation = 4.dp,
                    ) {
                        TabNavigationItem(tab = HomeTab)

                    }
                }

            },
        ) {
            CurrentTab()
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val title = tab.options.title
    BottomNavigationItem(
        modifier = Modifier,
        unselectedContentColor = Color.DarkGray,
        selectedContentColor = Color.LightGray,
        alwaysShowLabel = true,
        label = {
            Text(
                text = title,
            )
        },
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = {
            Icon(
                painter = tab.options.icon!!,
                contentDescription = tab.options.title
            )
        })
}

internal expect fun openUrl(url: String?)