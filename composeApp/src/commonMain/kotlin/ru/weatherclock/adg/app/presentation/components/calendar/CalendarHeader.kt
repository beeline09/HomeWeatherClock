package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.weatherclock.adg.app.presentation.components.calendar.stringAnnotation.toLocale
import ru.weatherclock.adg.app.presentation.components.calendar.styles.DateInputDefaults
import ru.weatherclock.adg.app.presentation.components.calendar.styles.getPointerCursor

@Composable
@Suppress("FunctionName")
internal fun CalendarHeader(
    initialDate: MutableState<LocalDateTime>,
    locale: DateInputDefaults.DateInputLocale
) {
    Icon(
        Icons.Outlined.ChevronLeft,
        "Previous month",
        tint = Color.White,
        modifier = Modifier
            .getPointerCursor()
            .clickable {
                val currentValue = initialDate.value
                initialDate.value = currentValue.date
                    .minus(
                        1,
                        DateTimeUnit.MONTH
                    )
                    .atTime(
                        currentValue.hour,
                        currentValue.minute,
                        currentValue.second
                    )
            }
    )
    Text(
        "${initialDate.value.dayOfMonth} ${initialDate.value.month.toLocale(locale)} ${initialDate.value.year}",
        color = Color.White,
        modifier = Modifier.width(200.dp),
        textAlign = TextAlign.Center
    )
    Icon(
        Icons.Outlined.ChevronRight,
        "Next month",
        tint = Color.White,
        modifier = Modifier
            .getPointerCursor()
            .clickable {
                val currentValue = initialDate.value
                initialDate.value = currentValue.date
                    .plus(
                        1,
                        DateTimeUnit.MONTH
                    )
                    .atTime(
                        currentValue.hour,
                        currentValue.minute,
                        currentValue.second
                    )
            }
    )
}