package ru.weatherclock.adg.app.presentation.components.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.setting_autocomplete_city_name_error_min_length
import homeweatherclock.composeapp.generated.resources.setting_autocomplete_city_name_hint
import homeweatherclock.composeapp.generated.resources.setting_autocomplete_city_not_found
import homeweatherclock.composeapp.generated.resources.setting_autocomplete_city_search_error
import homeweatherclock.composeapp.generated.resources.weather_no_api_key_exception
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.app.data.dto.CityConfig
import ru.weatherclock.adg.app.data.error.NoWeatherApiKeyException
import ru.weatherclock.adg.app.domain.model.settings.CitySearchState
import ru.weatherclock.adg.app.presentation.components.dialog.ClockBottomSheetDialog
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

/**
 * Поиск населённого пункта в bottom sheet. Состояние приходит из
 * CitySearchState: спиннер во время запроса, текст ошибки при неудаче,
 * «Населенный пункт не найден» при пустом результате.
 */
@Composable
fun CitySearchState.LocationsDialog(
    title: String,
    onTextChange: (String) -> Unit,
    dismissRequest: (CityConfig?) -> Unit
) {
    val colorsPalette = LocalCustomColorsPalette.current
    //Инпут всегда пустой при открытии; автозапрос предзаполненного города
    //не делаем (состояние поиска сбрасывается при открытии — см. item)
    var text by remember { mutableStateOf("") }
    var isError by rememberSaveable { mutableStateOf(false) }
    val charLimit = 3

    fun validate(value: String) {
        isError = value.length < charLimit
    }

    ClockBottomSheetDialog(title = title, onDismiss = { dismissRequest(null) }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                value = text,
                onValueChange = {
                    text = it
                    onTextChange.invoke(it)
                    validate(it)
                },
                label = {
                    Text(
                        text = stringResource(Res.string.setting_autocomplete_city_name_hint),
                        color = colorsPalette.toolbarColor
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = colorsPalette.clockText,
                    disabledTextColor = colorsPalette.clockText.copy(alpha = 0.5f),
                ),
                trailingIcon = {
                    if (isError) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            "error",
                            tint = colorsPalette.calendarWeekdayWeekendText
                        )
                    } else {
                        Icon(imageVector = Icons.Default.Clear,
                            contentDescription = "clear text",
                            modifier = Modifier.clickable {
                                text = ""
                            })
                    }
                },
                keyboardActions = KeyboardActions { validate(text) },
            )
            if (isError) {
                Text(
                    text = stringResource(Res.string.setting_autocomplete_city_name_error_min_length),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            when (val state = this@LocationsDialog) {
                CitySearchState.Loading -> Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is CitySearchState.Error -> Text(
                    text = if (state.cause is NoWeatherApiKeyException) {
                        stringResource(Res.string.weather_no_api_key_exception)
                    } else {
                        stringResource(Res.string.setting_autocomplete_city_search_error)
                    },
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                CitySearchState.Idle -> {}

                is CitySearchState.Results -> if (state.cities.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.setting_autocomplete_city_not_found),
                        fontSize = 15.sp,
                        color = colorsPalette.clockText,
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(items = state.cities) { currentValue ->
                            CityResultRow(
                                city = currentValue,
                                onClick = { dismissRequest.invoke(currentValue) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ColumnScope.CityResultRow(
    city: CityConfig,
    onClick: () -> Unit
) {
    val colorsPalette = LocalCustomColorsPalette.current
    val country = city.country
    val region = city.region
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
            .clickable(onClick = onClick),
    ) {
        Text(text = city.getNames(),
            color = colorsPalette.clockText,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth()
                .padding(top = 16.dp)
                .wrapContentHeight(align = Alignment.CenterVertically).let {
                    if (country.isBlank()) {
                        it.padding(bottom = 16.dp)
                    } else it
                })
        if (country.isNotBlank() || region.isNotBlank()) {
            Text(
                text = buildString {
                    if (country.isNotBlank()) {
                        append(country)
                    }
                    var notEmpty = false
                    if (isNotEmpty() && region.isNotBlank()) {
                        notEmpty = true
                        append(" (")
                    }
                    if (region.isNotBlank()) {
                        append(region)
                    }
                    if (notEmpty) {
                        append(")")
                    }
                },
                fontSize = 13.sp,
                color = colorsPalette.clockText,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 16.dp, top = 8.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }
    }
}
