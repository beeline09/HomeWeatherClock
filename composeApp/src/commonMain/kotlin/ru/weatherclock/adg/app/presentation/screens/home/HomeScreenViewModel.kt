package ru.weatherclock.adg.app.presentation.screens.home

import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus
import ru.weatherclock.adg.app.data.util.atEndOfDay
import ru.weatherclock.adg.app.data.util.atStartOfDay
import ru.weatherclock.adg.app.data.util.isEqualsByHour
import ru.weatherclock.adg.app.data.util.isEqualsByMinute
import ru.weatherclock.adg.app.data.util.timeStr
import ru.weatherclock.adg.app.domain.model.forecast.Severity
import ru.weatherclock.adg.app.domain.usecase.CalendarUseCase
import ru.weatherclock.adg.app.domain.usecase.ForecastUseCase
import ru.weatherclock.adg.app.presentation.components.calendar.dateTypes.now
import ru.weatherclock.adg.app.presentation.components.tickerFlow
import ru.weatherclock.adg.app.presentation.components.viewModel.ViewModelState

@ExperimentalCoroutinesApi
class HomeScreenViewModel(
    private val forecastUseCase: ForecastUseCase,
    private val calendarUseCase: CalendarUseCase,
): ViewModelState<HomeScreenState, HomeScreenIntent>(initialState = HomeScreenState()) {

    private val coroutineContextX: CoroutineContext = SupervisorJob() + Dispatchers.IO
    private var oneSecondTickJob: Job? = null
    private var oneMinuteJob: Job? = null
    private var oneHourJob: Job? = null
    private val timersScope = CoroutineScope(coroutineContextX)

    override fun intent(intent: HomeScreenIntent) {

    }

    //TODO вернуться сюда, чтобы правильно указывать регион!!!
    private suspend fun refreshCalendarData(region: Int) = safeScope.launch {
        val yearDays = calendarUseCase.getProdCalendarForThisYear(region)
        val currentDayTime = LocalDateTime.now()
        val calendarDay = currentDayTime.date
        val currentMonthDays = yearDays.filter { prodCalendarDay ->
            prodCalendarDay.date.year == calendarDay.year && prodCalendarDay.date.monthNumber == calendarDay.monthNumber
        }
        val currentDayProdCalendar = currentMonthDays.firstOrNull {
            it.date == calendarDay
        }
        setState {
            copy(
                prodCalendarDaysForCurrentMonth = currentMonthDays,
                currentProdDay = currentDayProdCalendar,
                dateTime = currentDayTime,
            )
        }
    }

    //TODO вернуться сюда и разобраться с forecastKey!!!
    private suspend fun refreshWeatherData(currentDateTime: LocalDateTime) = safeScope.launch {
        val forecast = forecastUseCase.getForPeriod(
            startDate = currentDateTime.atStartOfDay().date,
            endDate = currentDateTime.atEndOfDay().date.plus(DatePeriod(days = 5)),
            forecastKey = "291658"
        )
        setState {
            copy(
                forecast5Days = forecast?.dailyForecasts.orEmpty(),
                headline = forecast?.headline?.text,
                headlineSeverity = forecast?.headline?.severity ?: Severity.UNKNOWN
            )
        }
    }

    private var settingsJob: Job? = null
    fun showSettings() {
        setState {
            copy(settingsButtonShowed = true)
        }
        settingsJob = timersScope.launch {
            delay(5.seconds)
            hideSettings()
        }
    }

    fun hideSettings() {
        setState { copy(settingsButtonShowed = false) }
        settingsJob?.cancel()
        settingsJob = null
    }

    fun onLaunch() {
        oneSecondTickJob?.cancel()
        oneSecondTickJob = tickerFlow(period = 1.seconds)
            .map { LocalDateTime.now() }
            .onEach {
                val containsDots = state.value.time.contains(":")
                setState {
                    copy(time = it.timeStr(withDots = !containsDots))
                }
            }
            .launchIn(timersScope)
        oneMinuteJob?.cancel()
        oneMinuteJob = tickerFlow(period = 1.seconds)
            .map { LocalDateTime.now() }
            .distinctUntilChanged { old, new -> old.isEqualsByMinute(new) }
            .onEach {
                //Обновляем производственный календарь
                refreshCalendarData(1)
            }
            .launchIn(timersScope)
        oneHourJob?.cancel()
        oneHourJob = tickerFlow(period = 1.seconds)
            .map { LocalDateTime.now() }
            .distinctUntilChanged { old, new -> old.isEqualsByHour(new) }
            .onEach(::refreshWeatherData)
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    override fun dispose() {
        oneSecondTickJob?.cancel()
        oneMinuteJob?.cancel()
        oneHourJob?.cancel()
        timersScope.cancel()
    }
}