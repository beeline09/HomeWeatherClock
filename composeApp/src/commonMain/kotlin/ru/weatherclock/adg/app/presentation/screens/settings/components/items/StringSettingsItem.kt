package ru.weatherclock.adg.app.presentation.screens.settings.components.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.ContentAlpha
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.dialog_edittext_hint_enter_new_value
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.app.domain.model.settings.StringSetting
import ru.weatherclock.adg.app.presentation.components.dialog.ClockAlertDialog
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getDescription
import ru.weatherclock.adg.app.presentation.screens.settings.components.utils.getName
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Suppress("UnusedReceiverParameter")
@Composable
fun LazyItemScope.StringSettingsItem(item: StringSetting) {
    var showEditDialog by remember { mutableStateOf(false) }
    val description = item.settingsKey.getDescription()
    val alpha = if (item.isEnabled) 1f else ContentAlpha.disabled
    val colorsPalette = LocalCustomColorsPalette.current
    val title = item.settingsKey.getName()

    if (showEditDialog) {
        var text by remember { mutableStateOf(item.currentValue) }
        ClockAlertDialog(title = title,
            onPositiveClick = {
                item.onChange.invoke(text)
            },
            positiveButtonEnabled = text.isNotBlank(),
            dismissRequest = {
                showEditDialog = false
            }) {
            OutlinedTextField(
                value = text,
                enabled = item.isEnabled,
                onValueChange = { text = it },
                label = {
                    Text(
                        text = stringResource(Res.string.dialog_edittext_hint_enter_new_value),
                        color = colorsPalette.toolbarColor
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = colorsPalette.clockText,
                    disabledTextColor = colorsPalette.clockText.copy(alpha = 0.5f),

                    )
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.CenterVertically)
            .clickable(enabled = item.isEnabled) {
                showEditDialog = true
            },
    ) {
        Text(text = title,
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