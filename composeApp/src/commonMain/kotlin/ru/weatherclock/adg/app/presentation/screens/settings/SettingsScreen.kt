package ru.weatherclock.adg.app.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.app.domain.model.settings.BaseSettingItem
import ru.weatherclock.adg.app.presentation.screens.settings.components.backIcon
import ru.weatherclock.adg.app.presentation.screens.settings.components.getListItem
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
                        text = stringResource(MR.strings.settings_toolbar),
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
                        getListItem(
                            index,
                            item
                        )
                    }
                }
            }
        })
}