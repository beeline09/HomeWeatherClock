package ru.weatherclock.adg.app.presentation.screens.settings.components.items

import kotlinx.coroutines.delay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.dialog_api_keys_add_button
import homeweatherclock.composeapp.generated.resources.dialog_edittext_hint_enter_new_value
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.app.domain.model.settings.StringListSetting
import ru.weatherclock.adg.app.presentation.components.dialog.ClockAlertDialog
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getDescription
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Suppress("UnusedReceiverParameter")
@Composable
fun LazyItemScope.ListStringSettingsItem(item: StringListSetting) {
    var showDialog by remember { mutableStateOf(false) }
    val description = item.settingsKey.getDescription()
    val alpha = if (item.isEnabled) 1f else ContentAlpha.disabled
    val colorsPalette = LocalCustomColorsPalette.current
    val title = item.settingsKey.getName()

    if (showDialog) {
        var editedItems = item.currentValue.toMutableList().toList()
        ClockAlertDialog(title = title,
            dismissRequest = {
                showDialog = false
            },
            positiveButtonEnabled = true,
            onPositiveClick = {
//                println("New list: $editedItems")
                item.onChange.invoke(editedItems.filter { it.isNotBlank() })
            }) {
            EditableListString(initialItems = item.currentValue) {
                editedItems = it
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .clickable(enabled = item.isEnabled) {
                showDialog = true
            },
    ) {
        Text(
            text = title,
            color = colorsPalette.clockText,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .alpha(alpha = alpha)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
                .let {
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
                modifier = Modifier
                    .alpha(alpha = alpha)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(
                        bottom = 16.dp,
                        top = 8.dp
                    )
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }

    }
}

@Composable
private fun EditableListString(
    initialItems: List<String>,
    onListChange: (List<String>) -> Unit
) {
    val colorsPalette = LocalCustomColorsPalette.current
    var items by mutableStateOf(initialItems)
    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(1f)
        ) {
            items(items = items) {
                ListItem(apiKey = it,
                    onValueChanged = { old, new ->
                        val index = items.indexOf(old)
                        if (index >= 0) {
                            items = items.toMutableList().also { list ->
                                list[index] = new
                            }
                            onListChange(items)
                        }
                    },
                    onRemove = { s ->
                        items -= s
                        onListChange(items)
                    })
            }
        }

        //Кнопка добавления нового ключа
        Text(text = stringResource(Res.string.dialog_api_keys_add_button),
            modifier = Modifier
                .clickable {
                    if (items.none { it.isBlank() }) {
                        items += ""
                    }
                }
                .padding(all = 8.dp)
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.CenterVertically),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = colorsPalette.alertDialogDismissButton)
    }
}

@Composable
private fun ListItem(
    apiKey: String,
    onRemove: (String) -> Unit,
    onValueChanged: (oldValue: String, newValue: String) -> Unit,
) {
    val colorsPalette = LocalCustomColorsPalette.current
    var show by remember { mutableStateOf(true) }
    val currentItem by rememberUpdatedState(apiKey)
    val message = remember { mutableStateOf(apiKey) }/*    val dismissState = rememberDismissState(confirmValueChange = {
            if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                show = false
                true
            } else false
        },
            positionalThreshold = { 150.dp.toPx() })*/

    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        androidx.compose.material.OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .wrapContentHeight(align = Alignment.CenterVertically),
            value = message.value,
            onValueChange = {
                message.value = it
                onValueChanged(
                    apiKey,
                    it
                )
            },
            label = {
                androidx.compose.material3.Text(
                    text = stringResource(Res.string.dialog_edittext_hint_enter_new_value),
                    color = colorsPalette.toolbarColor
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = colorsPalette.clockText,
                disabledTextColor = colorsPalette.clockText.copy(alpha = 0.5f)
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .wrapContentWidth(align = Alignment.CenterHorizontally)
                .wrapContentWidth(align = Alignment.CenterHorizontally)
                .align(Alignment.CenterVertically)
                .clickable(enabled = message.value.isNotBlank()) {
                    show = false
                }
                .padding(all = 8.dp),
        ) {
            Icon(modifier = Modifier
                .wrapContentHeight(align = Alignment.CenterVertically)
                .wrapContentWidth(align = Alignment.CenterHorizontally)
                .align(Alignment.Center),
                imageVector = Icons.Default.Delete,
                contentDescription = "delete",
                tint = Color.Red.let {
                    if (message.value.isBlank()) {
                        it.copy(alpha = 0.5f)
                    } else it
                })
        }
    }

    /*    AnimatedVisibility(
            show,
            exit = fadeOut(spring())
        ) {
            SwipeToDismiss(state = dismissState,
                modifier = Modifier,
                background = {
                    DismissBackground(dismissState)
                },
                dismissContent = {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(align = Alignment.CenterVertically),
                        value = message.value,
                        onValueChange = {
                            message.value = it
                            onValueChanged(
                                apiKey,
                                it
                            )
                        },
                        label = {
                            androidx.compose.material3.Text(
                                text = stringResource(Res.string.dialog_edittext_hint_enter_new_value),
                                color = colorsPalette.toolbarColor
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorsPalette.clockText,
                            unfocusedTextColor = colorsPalette.clockText.copy(alpha = 0.8f),
                            disabledTextColor = colorsPalette.clockText.copy(alpha = 0.5f)
                        )
                    )
                })
        }*/

    LaunchedEffect(show) {
        if (!show) {
            delay(100)
            onRemove(currentItem)
        }
    }
}