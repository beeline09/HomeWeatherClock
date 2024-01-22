package ru.weatherclock.adg.app.presentation.screens.home

import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.app.data.util.atStartOfSecond
import ru.weatherclock.adg.app.data.util.hourStr
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.model.forecast.DailyForecast
import ru.weatherclock.adg.app.domain.model.forecast.Severity
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.app.presentation.components.viewModel.State

data class HomeScreenState(
    val hour: String = LocalDateTime.now().hourStr(),
    val minute: String = LocalDateTime.now().hourStr(),
    val dotsShowed: Boolean = true,
    val dateTime: LocalDateTime = LocalDateTime.now().atStartOfSecond(),
    val currentProdDay: ProdCalendarDay? = null,
    val prodCalendarDaysForCurrentMonth: List<ProdCalendarDay> = emptyList(),
    val forecast5Days: List<DailyForecast> = emptyList(),
    val headline: String? = null,
    val headlineSeverity: Severity = Severity.UNKNOWN,
    val settingsButtonShowed: Boolean = false,
    val hourlyBeepIncrement: Long = 0,
): State
