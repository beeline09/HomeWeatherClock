package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.LocalDateTime
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.DateInput
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.app.presentation.components.calendar.styles.DateInputDefaults
import ru.weatherclock.adg.app.presentation.components.calendar.styles.getBorderModifier
import ru.weatherclock.adg.app.presentation.components.calendar.styles.getPointerCursor

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Suppress("FunctionName")
fun GenericCalendarInput(
    dateHolder: MutableState<DateInput>,
    modifier: Modifier,
    colors: DateInputDefaults.DateInputColors,
    borders: DateInputDefaults.DateInputBorders,
    iconColor: Color,
    onDateSelected: (List<LocalDateTime?>) -> Unit,
    errorMessage: String,
    calendarBackground: Color,
    locale: DateInputDefaults.DateInputLocale,
    mask: VisualTransformation,
) {
    val popupOpened = remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }
    val dateTime = remember { mutableStateOf(LocalDateTime.now()) }
    var actualFieldValue by remember(dateHolder.value.getResult()) {
        mutableStateOf(
            dateHolder.value.displayInput()
        )
    }
    val groundFieldFocusRequester = remember { FocusRequester() }
    val borderModifier = modifier.getBorderModifier(
        focused = focused,
        dateHolder = dateHolder.value,
        colors = colors,
        borders = borders
    )
    val errorMessageState = remember { mutableStateOf<String?>(null) }

    Column(Modifier.height(0.dp).focusRequester(groundFieldFocusRequester)) {}

    Column {
        BasicTextField(
            value = actualFieldValue,
            onValueChange = { value ->
                val digitValue = value.filter { it.isDigit() }
                if (digitValue.length <= dateHolder.value.format.length) {
                    actualFieldValue = digitValue
                }
            },
            singleLine = true,
            decorationBox = { innerTextField ->
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "Open picker",
                        modifier = Modifier
                            .size(
                                18.dp,
                                18.dp
                            )
                            .getPointerCursor()
                            .clickable {
                                println("month icon clicked")
                                popupOpened.value = !popupOpened.value
                            },
                        tint = iconColor
                    )
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Clear filter",
                        modifier = Modifier
                            .size(
                                18.dp,
                                18.dp
                            )
                            .getPointerCursor()
                            .clickable {
                                dateHolder.value.clearInput()
                                dateTime.value = LocalDateTime.now()
                                errorMessageState.value = null
                                actualFieldValue = dateHolder.value.format
                                onDateSelected(dateHolder.value.getResult())
                            },
                        tint = iconColor
                    )
                }
                innerTextField()
            },
            cursorBrush = SolidColor(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                lineHeightStyle = LineHeightStyle(
                    LineHeightStyle.Alignment.Bottom,
                    LineHeightStyle.Trim.None
                )
            ),
            visualTransformation = mask,
            modifier = borderModifier
                .fillMaxWidth()
                .onFocusChanged {
                    focused = it.isFocused
                    if (it.isFocused) {
                        if (actualFieldValue == dateHolder.value.format) {
                            actualFieldValue = ""
                        }
                    } else {
                        if (actualFieldValue.isEmpty()) {
                            actualFieldValue = dateHolder.value.format
                        }
                    }
                }
                .padding(
                    vertical = 6.dp,
                    horizontal = 12.dp
                )
                .onKeyEvent {
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                        dateHolder.value.parseStringAndSetInput(
                            actualFieldValue,
                            errorMessageState,
                            onDateSelected,
                            errorMessage
                        )
                        errorMessageState.value = null
                        onDateSelected(dateHolder.value.getResult())
                        true
                    } else false
                }
        )
        errorMessageState.value?.let { message ->
            Text(
                text = message,
                fontSize = 12.sp,
                color = Color.Red
            )
        }
    }
    if (popupOpened.value) {
        Popup(popupPositionProvider = DesktopDropdownMenuPositionProvider(density = LocalDensity.current),
            onDismissRequest = {
                popupOpened.value = false
            },
            properties = PopupProperties(
                focusable = true
            ),
            onPreviewKeyEvent = { false },
            onKeyEvent = { false }) {
            CalendarPopup(
                dateTime = dateTime,
                dateHolder = dateHolder,
                popupOpened = popupOpened,
                groundFieldFocusRequester = groundFieldFocusRequester,
                onDateSelected = onDateSelected,
                errorMessage = errorMessageState,
                background = calendarBackground,
                locale = locale
            )
        }
    }
}