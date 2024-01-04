package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.LocalDateTime
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.DateInput
import ru.weatherclock.adg.app.presentation.components.calendar.styles.getPointerCursor
import ru.weatherclock.adg.app.presentation.components.text.AutoSizeText
import ru.weatherclock.adg.theme.LocalCustomColorsPalette

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MonthGrid(
    dateTime: LocalDateTime,
    dateHolder: DateInput,
    dates: List<List<Int?>>,
    prodCalendarDays: List<ProdCalendarDay>,
    onDateSelected: CalendarCallback,
) {

    val colorPalette = LocalCustomColorsPalette.current
    dates.filter { it.isNotEmpty() }.forEach { week ->
        Row(Modifier.fillMaxWidth()) {
            week.forEachIndexed { dayPosInWeek, date ->
                var hovered by remember { mutableStateOf(false) }
                val selected by derivedStateOf {
                    date?.let {
                        dateHolder.checkIfSelected(
                            date,
                            dateTime
                        )
                    } ?: false
                }
                val defColor = when {
                    selected -> colorPalette.calendarDaySelectedText
                    dayPosInWeek in 5..6 -> colorPalette.calendarWeekdayWeekendText
                    else -> colorPalette.calendarDayDefaultText
                }
                val currentProdDay = prodCalendarDays.firstOrNull { it.date.dayOfMonth == date }
                val borderColor = when {
                    hovered -> colorPalette.calendarDayHover
                    date.isCurrentDay(dateTime) -> colorPalette.calendarWeekdayBorder
                    else -> Color.Transparent
                }

                val interactionSource1 = remember { MutableInteractionSource() }
                val interactions = remember { mutableStateListOf<Interaction>() }

                LaunchedEffect(interactionSource1) {
                    interactionSource1.interactions.collect { interaction ->
                        when (interaction) {
                            is PressInteraction.Press -> {
                                interactions.add(interaction)
                            }
                        }
                    }
                }

                val rippleColor = Color.White
                val indication1 = CustomIndication(
                    pressColor = colorPalette.calendarDaySelectedBackground,
                    alpha = .5f,
                )
                val textColor =
                    if (date in prodCalendarDays.map { it.date.dayOfMonth } && !selected) {
                        val prodDay = prodCalendarDays.firstOrNull { it.date.dayOfMonth == date }
                        prodDay?.color() ?: defColor
                    } else defColor

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
                            date?.let { hovered = true }
                        }
                        .onPointerEvent(eventType = PointerEventType.Exit) {
                            date?.let { hovered = false }
                        }
                        .clickable(
                            enabled = date != null,
                            interactionSource = interactionSource1,
                            indication = indication1,
                        ) {
                            dateHolder.select(
                                date!!,
                                dateTime,
                                mutableStateOf(CalendarWindowState.CALENDAR)
                            )
                            (dateHolder as? DateInput.SingleDate)?.date?.value
                                ?.let {
                                    onDateSelected(
                                        it,
                                        currentProdDay
                                    )
                                }
                        }
                        .indication(
                            interactionSource = interactionSource1,
                            indication = rememberRipple(
                                color = rippleColor,
                                radius = 8.dp
                            )
                        )
                        .background(
                            if (selected) Color.Red else Color.Transparent
                        )
                        .padding(vertical = 2.dp),
                    alignment = Alignment.Center,
//                                textAlign = TextAlign.Center
                )
            }
        }
    }
}