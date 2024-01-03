package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.LocalDateTime
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.DateInput

@Composable
@Suppress("FunctionName")
fun Calendar(
    dateTime: LocalDateTime,
    dateHolder: DateInput,
    prodCalendarDays: List<ProdCalendarDay>,
    onDateSelected: (Long) -> Unit,
) {
    val dates = dateTime.getMonthGrid()
    Column(
        Modifier
            .wrapContentHeight(align = Alignment.CenterVertically)
            .wrapContentWidth(align = Alignment.CenterHorizontally)
            .aspectRatio(1f)
    ) {
        weekHeader()
        Spacer(Modifier.height(5.dp))
        MonthGrid(
            dateTime,
            dateHolder,
            dates,
            prodCalendarDays,
            onDateSelected
        )
    }
}

enum class CalendarWindowState {
    CALENDAR,
    TIME
}

@ExperimentalComposeUiApi
fun Modifier.onPointerEvent(
    eventType: PointerEventType,
    pass: PointerEventPass = PointerEventPass.Main,
    onEvent: AwaitPointerEventScope.(event: PointerEvent) -> Unit
): Modifier = composed {
    val currentEventType by rememberUpdatedState(eventType)
    val currentOnEvent by rememberUpdatedState(onEvent)
    pointerInput(pass) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(pass)
                if (event.type == currentEventType) {
                    currentOnEvent(event)
                }
            }
        }
    }
}