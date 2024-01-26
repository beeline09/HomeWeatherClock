package ru.weatherclock.adg.app.presentation.components.radiobutton

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import ru.weatherclock.adg.app.presentation.components.dialog.ClockAlertDialog

@Composable
fun <T> List<T>.RadioGroupDialog(
    title: String,
    converter: (T) -> String,
    isEnabled: Boolean,
    isChecked: (T) -> Boolean,
    dismissRequest: (T?) -> Unit
) {
    ClockAlertDialog(title = title,
        dismissRequest = {
            dismissRequest(null)
        }) {
        LazyColumn {
            items(items = this@RadioGroupDialog) { currentValue ->
                radioButton(
                    isEnabled = isEnabled,
                    isChecked = isChecked(currentValue),
                    converter = converter,
                    item = currentValue,
                    onCheckedChange = dismissRequest
                )
            }
        }
    }
}