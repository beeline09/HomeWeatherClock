package ru.weatherclock.adg.app.presentation.components.calendar

import kotlinx.datetime.LocalDateTime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.DateTypes
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.initializeInput
import ru.weatherclock.adg.app.presentation.components.calendar.inputMask.DateRangeMaskTransformation
import ru.weatherclock.adg.app.presentation.components.calendar.styles.DateInputDefaults
import ru.weatherclock.adg.app.presentation.components.calendar.styles.enabled
import ru.weatherclock.adg.app.presentation.components.calendar.styles.getDefaultErrorMessage
import ru.weatherclock.adg.app.presentation.components.calendar.styles.selected

@Composable
@Suppress("FunctionName")
fun DateRangePicker(
    modifier: Modifier = Modifier,
    colors: DateInputDefaults.DateInputColors = DateInputDefaults.DateInputColors(),
    borders: DateInputDefaults.DateInputBorders = DateInputDefaults.DateInputBorders(),
    locale: DateInputDefaults.DateInputLocale = DateInputDefaults.DateInputLocale.EN,
    errorMessage: String = getDefaultErrorMessage(locale),
    iconColor: Color = enabled,
    calendarBackground: Color = selected,
    onDateSelected: (List<LocalDateTime?>) -> Unit,
) {
    val dateHolder = remember {
        mutableStateOf(
            initializeInput(
                DateTypes.DATE_RANGE,
                locale
            )
        )
    }
    GenericCalendarInput(
        dateHolder = dateHolder,
        modifier = modifier,
        colors = colors,
        borders = borders,
        iconColor = iconColor,
        onDateSelected = onDateSelected,
        errorMessage = errorMessage,
        calendarBackground = calendarBackground,
        locale = locale,
        mask = DateRangeMaskTransformation()
    )
}