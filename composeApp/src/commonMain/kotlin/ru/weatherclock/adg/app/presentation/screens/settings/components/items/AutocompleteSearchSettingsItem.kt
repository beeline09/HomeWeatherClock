package ru.weatherclock.adg.app.presentation.screens.settings.components.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.setting_description_city
import homeweatherclock.composeapp.generated.resources.setting_description_city_empty
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.app.data.dto.CityConfig
import ru.weatherclock.adg.app.data.dto.WeatherServer
import ru.weatherclock.adg.app.domain.model.settings.AutoCompleteSearchSetting
import ru.weatherclock.adg.app.presentation.components.search.LocationsDialog
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@OptIn(FlowPreview::class)
@Suppress("UnusedReceiverParameter")
@Composable
fun LazyItemScope.AutocompleteSearchSettingsItem(item: AutoCompleteSearchSetting) {
    var showRegionsDialog by remember { mutableStateOf(false) }
    val name = buildString {
        append(item.currentValue.getNames())
        if (item.currentValue.country.isNotBlank()) {
            append(", ")
            append(item.currentValue.country)
        }
        if (item.currentValue.region.isNotBlank()) {
            append(", ")
            append(item.currentValue.region)
        }
    }
    val description = if (item.currentServer == WeatherServer.Accuweather) {
        if (item.currentValue.key.isNotBlank()) {
            stringResource(Res.string.setting_description_city, name)
        } else stringResource(Res.string.setting_description_city_empty)
    } else {
        if (item.currentValue.latitude != 0.0 && item.currentValue.longitude != 0.0) {
            stringResource(Res.string.setting_description_city, name)
        } else stringResource(Res.string.setting_description_city_empty)
    }
    val alpha = if (item.isEnabled) 1f else ContentAlpha.disabled
    val colorsPalette = LocalCustomColorsPalette.current
    val title = item.settingsKey.getName()
    var autocompleteItems by remember { mutableStateOf<List<CityConfig>>(emptyList()) }
    LaunchedEffect(Unit) {
        item.searchResults.collectLatest {
            autocompleteItems = it
        }
    }

    if (showRegionsDialog && item.isEnabled) {
        autocompleteItems.LocationsDialog(
            title = title,
            dismissRequest = {
                showRegionsDialog = false
                if (it != null) {
                    item.onChange.invoke(it)
                }
            },
            onTextChange = item.onSearchTextChanged,
            initialValue = item.currentValue.localizedName ?: item.currentValue.name
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically)
            .clickable(enabled = item.isEnabled) {
                showRegionsDialog = true
            },
    ) {
        Text(text = title,
            color = colorsPalette.clockText,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alpha(alpha = alpha).fillMaxWidth().padding(horizontal = 16.dp)
                .padding(top = 16.dp).wrapContentHeight(align = Alignment.CenterVertically).let {
                    if (description.isBlank()) {
                        it.padding(bottom = 16.dp)
                    } else it
                })
        if (description.isNotBlank()) {
            Text(
                text = description,
                fontSize = 13.sp,
                color = colorsPalette.clockText,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.alpha(alpha = alpha).fillMaxWidth().padding(horizontal = 16.dp)
                    .padding(
                        bottom = 16.dp, top = 8.dp
                    ).wrapContentHeight(align = Alignment.CenterVertically)
            )
        }

    }
}