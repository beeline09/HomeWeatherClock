package ru.weatherclock.adg.app.presentation.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import homeweatherclock.composeapp.generated.resources.Res
import homeweatherclock.composeapp.generated.resources.dialog_button_cancel
import homeweatherclock.composeapp.generated.resources.dialog_button_ok
import org.jetbrains.compose.resources.stringResource
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
fun ClockAlertDialog(
    title: String,
    dismissRequest: () -> Unit,
    dismissButtonText: String = stringResource(Res.string.dialog_button_cancel),
    positiveButtonText: String = stringResource(Res.string.dialog_button_ok),
    onPositiveClick: (() -> Unit)? = null,
    positiveButtonEnabled: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorsPalette = LocalCustomColorsPalette.current
    androidx.compose.material3.AlertDialog(
        containerColor = colorsPalette.alertDialogBackground,
        shape = RoundedCornerShape(size = 22.dp),
        modifier = Modifier.safeContentPadding().padding(all = 25.dp),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        ),
        onDismissRequest = dismissRequest,
        confirmButton = {
            if (onPositiveClick != null) {
                Text(
                    text = positiveButtonText,
                    modifier = Modifier.clickable(
                        enabled = positiveButtonEnabled,
                        onClick = {
                            dismissRequest()
                            onPositiveClick()
                        }
                    ).padding(all = 8.dp),
                    color = colorsPalette.alertDialogPositiveButton.copy(alpha = if (positiveButtonEnabled) 1f else 0.5f)
                )
            }
        },
        dismissButton = {
            Text(
                text = dismissButtonText,
                modifier = Modifier.clickable {
                    dismissRequest()
                }.padding(all = 8.dp),
                color = colorsPalette.alertDialogDismissButton
            )
        },
        title = {
            Text(
                text = title,
                modifier = Modifier.padding(all = 16.dp),
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodySmall,
                color = colorsPalette.clockText
            )
        },
        text = {
            Column {
                Spacer(modifier = Modifier.height(1.dp))
                content()
            }
        })
}