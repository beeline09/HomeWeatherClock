package ru.weatherclock.adg.app.presentation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ru.weatherclock.adg.app.presentation.screens.home.HomeScreen

object SettingsTab: Tab {

    @Composable
    override fun Content() {
        HomeScreen()
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Rounded.Settings)

            return remember {
                TabOptions(
                    index = 1u,
                    title = "Settings",
                    icon = icon
                )
            }
        }

}
