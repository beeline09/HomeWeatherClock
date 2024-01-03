package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.DateInput
import ru.weatherclock.adg.app.presentation.components.calendar.styles.getPointerCursor
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MonthGrid(
    dateTime: MutableState<LocalDateTime>,
    dateHolder: MutableState<DateInput>,
    dates: List<List<Int?>>,
    onDateSelected: (Long) -> Unit
) {
    val colorPalette = LocalCustomColorsPalette.current
    dates.filter { it.isNotEmpty() }.forEach { week ->
        Row(Modifier.fillMaxWidth()) {
            week.forEachIndexed { dayPosInWeek, date ->
                var hovered by remember { mutableStateOf(false) }
                val selected by derivedStateOf {
                    date?.let {
                        dateHolder.value.checkIfSelected(
                            date,
                            dateTime
                        )
                    } ?: false
                }
                val textColor = when {
                    selected -> colorPalette.calendarDaySelectedText
                    dayPosInWeek in 5..6 -> colorPalette.calendarWeekdayWeekendText
                    else -> colorPalette.calendarDayDefaultText
                }
                val borderColor = when {
                    hovered -> colorPalette.calendarDayHover
                    date.isCurrentDay(dateTime.value) -> colorPalette.calendarWeekdayBorder
                    else -> Color.Transparent
                }
                AutoSizeText(
                    text = (date?.toString() ?: ""),
                    minTextSize = 5.sp,
                    maxTextSize = 50.sp,
                    stepGranularityTextSize = 5.sp,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,
//                                fontSize = 12.sp,
                    color = textColor,
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
                            borderColor,
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
                                mutableStateOf(CalendarWindowState.CALENDAR)
                            )
                            (dateHolder.value as? DateInput.SingleDate)?.date?.value
                                ?.toInstant(TimeZone.currentSystemDefault())
                                ?.toEpochMilliseconds()
                                ?.let {
                                    onDateSelected(it)
                                }
                        }
                        .background(if (selected) colorPalette.calendarDaySelectedBackground else Color.Transparent)
                        .padding(vertical = 2.dp),
                    alignment = Alignment.Center
//                                textAlign = TextAlign.Center
                )
            }
        }
    }
}