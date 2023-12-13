package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.LocalDateTime
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.DateInput
import ru.weatherclock.adg.app.presentation.components.calendar.styles.DateInputDefaults
import ru.weatherclock.adg.app.presentation.components.calendar.styles.getPointerCursor
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Suppress("FunctionName")
fun Calendar(
    dateTime: MutableState<LocalDateTime>,
    dateHolder: MutableState<DateInput>,
//    groundFieldFocusRequester: FocusRequester,
//    popupOpened: MutableState<Boolean>,
    onDateSelected: (List<LocalDateTime?>) -> Unit,
    errorMessage: MutableState<String?>,
    background: Color,
    locale: DateInputDefaults.DateInputLocale,
) {
    val windowState = remember { mutableStateOf(CalendarWindowState.CALENDAR) }
    val textSize = remember { mutableStateOf(50.sp.value) }
    when (windowState.value) {
        CalendarWindowState.CALENDAR -> {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(32.dp),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                CalendarHeader(
//                    dateTime,
//                    locale
//                )
//            }
            val dates = dateTime.value.getMonthGrid()
            Column(
                Modifier
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .aspectRatio(1f)
                    .background(Color.Gray)
            ) {
                weekHeader()
                Spacer(Modifier.height(5.dp))
                dates.filter { it.isNotEmpty() }.forEach { week ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        week.forEach { date ->
                            var hovered by remember { mutableStateOf(false) }
                            val selected by derivedStateOf {
                                date?.let {
                                    dateHolder.value.checkIfSelected(
                                        date,
                                        dateTime
                                    )
                                } ?: false
                            }
                            AutoSizeText(
                                text = (date?.toString() ?: ""),
                                minTextSize = 5.sp,
                                maxTextSize = 50.sp,
                                stepGranularityTextSize = 5.sp,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
//                                fontSize = 12.sp,
                                color = if (selected) Color.Black else Color.White,
                                modifier = Modifier
                                    .also {
                                        if (date != null) {
                                            it.getPointerCursor()
                                        }
                                    }
                                    .weight(1f)
                                    .aspectRatio(1f)
//                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(percent = 50))
                                    .border(
                                        1.dp,
                                        if (hovered) Color.White else Color.Transparent,
                                        RoundedCornerShape(percent = 50)
                                    )
//                                    .background(Color.Blue)
                                    .onPointerEvent(eventType = PointerEventType.Enter) {
                                        date?.let {
                                            hovered = true
                                        }
                                    }
                                    .onPointerEvent(eventType = PointerEventType.Exit) {
                                        date?.let {
                                            hovered = false
                                        }
                                    }
                                    .clickable(
                                        enabled = date != null
                                    ) {
                                        dateHolder.value.select(
                                            date!!,
                                            dateTime,
                                            windowState
                                        )
                                    }
                                    .background(if (selected) Color.White else Color.Transparent)
                                    .padding(vertical = 2.dp),
                                alignment = Alignment.Center
//                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        CalendarWindowState.TIME -> {
            TimeInput(
                dateHolder = dateHolder,
//                groundFieldFocusRequester = groundFieldFocusRequester,
//                popupOpened = popupOpened,
                windowState = windowState,
                errorMessage = errorMessage,
                locale = locale,
                onDateSelected = onDateSelected,
                background = background
            )
        }
    }

    /*if (dateHolder.value !is DateInput.SingleDateTime) {
        Button(
            onClick = {
                onDateSelected(dateHolder.value.getResult())
//                groundFieldFocusRequester.requestFocus()
                errorMessage.value = null
//                popupOpened.value = false
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = background),
            modifier = Modifier
                .fillMaxWidth()
                .getPointerCursor()
        ) {
            Text(
                getApplyText(locale),
                color = Color.White,
                modifier = Modifier
            )
        }
    }*/
}