package ru.weatherclock.adg.app.presentation.screens.home

import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import ru.weatherclock.adg.app.data.util.atEndOfDay
import ru.weatherclock.adg.app.data.util.atStartOfDay
import ru.weatherclock.adg.app.data.util.isEqualsByDayOfMonth
import ru.weatherclock.adg.app.data.util.isEqualsByHour
import ru.weatherclock.adg.app.data.util.isEqualsByMinute
import ru.weatherclock.adg.app.data.util.isEqualsByMonthNumber
import ru.weatherclock.adg.app.domain.model.forecast.Severity
import ru.weatherclock.adg.app.domain.usecase.CalendarUseCase
import ru.weatherclock.adg.app.domain.usecase.ForecastUseCase
import ru.weatherclock.adg.app.domain.usecase.SettingsUseCase
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.app.presentation.components.tickerFlow
import ru.weatherclock.adg.app.presentation.components.viewModel.ViewModelState

@ExperimentalCoroutinesApi
class HomeScreenViewModel(
    private val forecastUseCase: ForecastUseCase,
    private val calendarUseCase: CalendarUseCase,
    private val settingsUseCase: SettingsUseCase,
): ViewModelState<HomeScreenState, HomeScreenIntent>(initialState = HomeScreenState()) {

    private var oneSecondTickJob: Job? = null
    private var settingsJob: Job? = null
    private var allSettingsJob: Job? = null

    override fun intent(intent: HomeScreenIntent) {
        when (intent) {
            HomeScreenIntent.Settings.Hide -> hideSettings()
            HomeScreenIntent.Settings.Show -> showSettings()
        }
    }

    private suspend fun refreshCalendarData() = safeScope.launch {
        val currentDayTime = LocalDateTime.now()
        if (calendarUseCase.isProdCalendarEnabled()) {
            val yearDays = calendarUseCase.getProdCalendar()
            val calendarDay = currentDayTime.date
            val prodCalendarDaysForCurrentMonth = yearDays.filter { prodCalendarDay ->
                prodCalendarDay.date.isEqualsByMonthNumber(calendarDay)
            }
            val currentDayProdCalendar = prodCalendarDaysForCurrentMonth.firstOrNull {
                it.date.isEqualsByDayOfMonth(calendarDay)
            }
            setState {
                copy(
                    prodCalendarDaysForCurrentMonth = prodCalendarDaysForCurrentMonth,
                    currentProdDay = currentDayProdCalendar,
                    dateTime = currentDayTime,
                )
            }
        } else {
            setState {
                copy(
                    prodCalendarDaysForCurrentMonth = emptyList(),
                    currentProdDay = null,
                    dateTime = currentDayTime
                )
            }
        }
    }

    private suspend fun refreshWeatherData(currentDateTime: LocalDateTime) = safeScope.launch {
        val forecast = forecastUseCase.getForPeriod(
            startDate = currentDateTime.atStartOfDay(),
            endDate = currentDateTime.atEndOfDay().date
                .plus(DatePeriod(days = 5))
                .atTime(
                    hour = 23,
                    minute = 59,
                    second = 59
                )
        )
        setState {
            copy(
                forecast5Days = forecast?.dailyForecast.orEmpty(),
                headline = forecast?.headline,
                headlineSeverity = forecast?.severity ?: Severity.UNKNOWN
            )
        }
    }

    private fun showSettings() {
        setState {
            copy(settingsButtonShowed = true)
        }
        settingsJob = safeScope.launch {
            delay(5.seconds)
            hideSettings()
        }
    }

    private fun hideSettings() {
        setState { copy(settingsButtonShowed = false) }
        settingsJob?.cancel()
        settingsJob = null
    }

    private var oneMinuteTime: LocalDateTime = LocalDateTime.now()
    private var oneHourTime: LocalDateTime = LocalDateTime.now()

    fun onLaunch() {
        allSettingsJob?.cancel()
        allSettingsJob = settingsUseCase.getAllSettingsFlow().onEach {
            setState {
                copy(
                    appSettings = it
                )
            }
        }.launchIn(safeScope)
        oneSecondTickJob?.cancel()
        oneSecondTickJob = tickerFlow(period = 1.seconds).map { LocalDateTime.now() }.onStart {
            val currentTime = LocalDateTime.now()
            oneMinuteTime = currentTime
            refreshCalendarData()
            oneHourTime = currentTime
            refreshWeatherData(currentTime)
        }.onEach {
            setState {
                copy(
                    dotsShowed = !dotsShowed,
                    hour = it.hour,
                    minute = it.minute
                )
            }
            val s = state.value
            val condition1 =
                s.prodCalendarDaysForCurrentMonth.isEmpty() && !it.isEqualsByMinute(oneMinuteTime)
            val condition2 =
                s.prodCalendarDaysForCurrentMonth.isNotEmpty() && !it.isEqualsByDayOfMonth(oneMinuteTime)
            if (condition1 || condition2) {
                oneMinuteTime = it
                refreshCalendarData()
            }

            if (!it.isEqualsByHour(oneHourTime)) {
                if (settingsUseCase.isHourlyBeepAllowed(currentHour = it.hour)) {
                    setState {
                        copy(hourlyBeepIncrement = hourlyBeepIncrement + 1L)
                    }
                } else {
                    setState {
                        copy(hourlyBeepIncrement = 0)
                    }
                }
                oneHourTime = it
                refreshWeatherData(it)
            }
        }.launchIn(safeScope)
    }

    override fun dispose() {
        settingsJob?.cancel()
        allSettingsJob?.cancel()
        oneSecondTickJob?.cancel()
    }
}