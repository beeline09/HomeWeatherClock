package ru.weatherclock.adg.app.presentation.tabs

import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.icerock.moko.resources.compose.stringResource
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.presentation.screens.home.HomeScreen

object HomeTab: Tab {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Composable
    override fun Content() {
        HomeScreen()
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Rounded.Home)
            val title = stringResource(MR.strings.home_toolbar)
            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

}
