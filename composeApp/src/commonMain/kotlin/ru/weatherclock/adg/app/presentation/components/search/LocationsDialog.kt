package ru.weatherclock.adg.app.presentation.components.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.setting_autocomplete_city_name_error_min_length
import homeweatherclock.composeapp.generated.resources.setting_autocomplete_city_name_hint
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.app.data.dto.CityConfig
import ru.weatherclock.adg.app.presentation.components.dialog.ClockAlertDialog
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
fun List<CityConfig>.LocationsDialog(
    initialValue: String,
    title: String,
    onTextChange: (String) -> Unit,
    dismissRequest: (CityConfig?) -> Unit
) {
    val colorsPalette = LocalCustomColorsPalette.current
    var text by remember { mutableStateOf(initialValue) }
    var isError by rememberSaveable { mutableStateOf(false) }
    val charLimit = 3
    fun validate(text: String) {
        isError = text.length < charLimit
    }

    if (initialValue.isNotBlank() && initialValue == text) {
        onTextChange.invoke(initialValue)
    }

    ClockAlertDialog(title = title, dismissRequest = { dismissRequest(null) }) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth()
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
            LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                items(items = this@LocationsDialog) { currentValue ->
                    val country = currentValue.country
                    val region = currentValue.region
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                            .wrapContentHeight(align = Alignment.CenterVertically).clickable() {
                                dismissRequest.invoke(currentValue)
                            },
                    ) {
                        Text(text = currentValue.getNames(),
                            color = colorsPalette.clockText,
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
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
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                    .padding(
                                        bottom = 16.dp, top = 8.dp
                                    ).wrapContentHeight(align = Alignment.CenterVertically)
                            )
                        }

                    }
                }
            }
        }
    }
}