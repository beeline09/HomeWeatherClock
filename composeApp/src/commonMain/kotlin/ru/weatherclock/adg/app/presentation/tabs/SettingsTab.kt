package ru.weatherclock.adg.app.presentation.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.icerock.moko.resources.compose.stringResource
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.presentation.screens.settings.SettingsScreen

object SettingsTab: Tab {

    @Composable
    override fun Content() {
        SettingsScreen()
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Rounded.Settings)
            val title = stringResource(MR.strings.settings_toolbar)
            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

}
