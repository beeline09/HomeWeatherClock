package ru.weatherclock.adg.app.presentation.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import dev.icerock.moko.resources.compose.stringResource
import ru.weatherclock.adg.MR
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@Composable
fun ClockAlertDialog(
    title: String,
    dismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val colorsPalette = LocalCustomColorsPalette.current
    androidx.compose.material3.AlertDialog(containerColor = colorsPalette.alertDialogBackground,
        shape = RoundedCornerShape(size = 22.dp),
        modifier = Modifier.safeContentPadding().padding(all = 25.dp),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        ),
        onDismissRequest = dismissRequest,
        confirmButton = {},
        dismissButton = {
            Text(
                text = stringResource(MR.strings.dialog_button_cancel),
                modifier = Modifier.clickable {
                    dismissRequest()
                }.padding(all = 8.dp),
                color = colorsPalette.clockText
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