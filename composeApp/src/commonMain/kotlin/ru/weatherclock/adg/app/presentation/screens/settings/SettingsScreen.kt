package ru.weatherclock.adg.app.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.settings_toolbar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.weatherclock.adg.app.domain.model.settings.BaseSettingItem
import ru.weatherclock.adg.app.domain.model.settings.SettingKey
import ru.weatherclock.adg.app.presentation.screens.settings.components.SettingsListItem
import ru.weatherclock.adg.app.presentation.screens.settings.components.backIcon
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
fun SettingsScreen(screenModel: SettingsScreenViewModel = koinInject()) {
    val navigator = LocalNavigator.currentOrThrow
    val colorsPalette = LocalCustomColorsPalette.current
    val state by screenModel.state.collectAsState(SettingsScreenState())
    val settings = state.settings
    Scaffold(
        modifier = Modifier.background(color = colorsPalette.background),
        topBar = {
            TopAppBar(
                backgroundColor = colorsPalette.toolbarColor,
                title = {
                    Text(
                        text = stringResource(Res.string.settings_toolbar),
                        color = colorsPalette.background
                    )
                },
                navigationIcon = navigator.backIcon()
            )
        },
        content = {
            Box(
                modifier = Modifier.background(color = colorsPalette.background).fillMaxSize()
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp
                    )
                ) {
                    itemsIndexed(items = settings,
                        key = { _, item -> item.settingsKey }) { index, item: BaseSettingItem ->
                        SettingsListItem(item)
                        if (index < settings.lastIndex && index > 0 && item.settingsKey !in SettingKey.headers) {
                            val nextItem = settings[index + 1]
                            if (nextItem.settingsKey !in SettingKey.headers) {
                                Spacer(
                                    modifier = Modifier.fillMaxWidth().height(1.dp).padding(
                                        start = 16.dp,
                                        end = 16.dp
                                    ).background(color = colorsPalette.divider)
                                )
                            }
                        }
                    }
                }
            }
        })
}