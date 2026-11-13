package ru.weatherclock.adg.app.presentation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.home_toolbar
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.app.presentation.screens.home.HomeScreen
import ru.weatherclock.adg.app.presentation.screens.home.HomeScreenViewModel

object HomeTab: Tab {

    @Composable
    override fun Content() {
        // Tab : Screen → вьюмодель привязана к экрану Voyager: при уходе
        // onDispose отменяет её скоуп (фабрика в Koin, не single)
        HomeScreen(screenModel = getScreenModel())
    }


    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Rounded.Home)
            val title = stringResource(Res.string.home_toolbar)
            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

}
