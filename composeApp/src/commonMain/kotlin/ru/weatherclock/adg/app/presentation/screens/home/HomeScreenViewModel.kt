package ru.weatherclock.adg.app.presentation.screens.home

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.app.data.util.atStartOfSecond
import ru.weatherclock.adg.app.domain.util.now
import ru.weatherclock.adg.app.data.util.isEqualsByDayOfMonth
import ru.weatherclock.adg.app.data.util.isEqualsByMonthNumber
import ru.weatherclock.adg.app.domain.model.forecast.Forecast
import ru.weatherclock.adg.app.domain.usecase.CalendarUseCase
import ru.weatherclock.adg.app.domain.usecase.ForecastUseCase
import ru.weatherclock.adg.app.domain.usecase.SettingsUseCase
import ru.weatherclock.adg.app.presentation.components.tickerFlow
import ru.weatherclock.adg.app.presentation.components.viewModel.ViewModelState
import kotlin.time.Duration.Companion.seconds

class HomeScreenViewModel(
    private val forecastUseCase: ForecastUseCase,
    private val calendarUseCase: CalendarUseCase,
    private val settingsUseCase: SettingsUseCase,
): ViewModelState<HomeScreenState, HomeScreenIntent>(initialState = HomeScreenState()) {

    // ---------------------------------------------------------------------
    // Быстрые величины — отдельные потоки: StateFlow дедуплицирует по
    // equals, поэтому часы рекомпозируются ТОЛЬКО при смене минуты, а
    // точки — только при мигании (выключено мигание — не тикает ничего)
    // ---------------------------------------------------------------------

    /** ЧЧ:ММ — обновляется при смене минуты */
    private val _hourMinute = MutableStateFlow(currentTime().let { it.hour to it.minute })
    val hourMinute: StateFlow<Pair<Int, Int>> = _hourMinute

    /** Мигание разделителя: флип каждую секунду, только если включено */
    private val _dotsOn = MutableStateFlow(true)
    val dotsOn: StateFlow<Boolean> = _dotsOn

    /** Ежечасный бип (в разрешённых часах) — событие, не счётчик */
    private val _hourlyBeep = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val hourlyBeep: SharedFlow<Unit> = _hourlyBeep

    /** Погода — самообновляющийся источник use case'а (B) */
    val forecast: StateFlow<Forecast?> = forecastUseCase.forecast

    /** События для тостов (нет API-ключа и т.п.) */
    val forecastErrors = forecastUseCase.errors

    private var oneSecondTickJob: Job? = null
    private var settingsJob: Job? = null
    private var allSettingsJob: Job? = null

    override fun intent(intent: HomeScreenIntent) {
        when (intent) {
            HomeScreenIntent.Settings.Hide -> hideSettings()
            HomeScreenIntent.Settings.Show -> showSettings()
        }
    }

    private fun refreshCalendarData() = safeScope.launch {
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
                    date = calendarDay,
                )
            }
        } else {
            setState {
                copy(
                    prodCalendarDaysForCurrentMonth = emptyList(),
                    currentProdDay = null,
                    date = currentDayTime.date
                )
            }
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

    fun onLaunch() {
        refreshCalendarData()
        allSettingsJob?.cancel()
        allSettingsJob = settingsUseCase.getAllSettingsFlow().onEach {
            setState {
                copy(appSettings = it)
            }
        }.launchIn(safeScope)

        oneSecondTickJob?.cancel()
        oneSecondTickJob = safeScope.launch {
            // Инициализация ТЕКУЩИМИ значениями: первый тик не считает
            // себя «сменой минуты/часа/дня» (календарь уже рефрешнут
            // в onLaunch — раньше стартовал двойной запрос)
            val start = currentTime()
            var lastMinute: Int = start.minute
            var lastHour: Int = start.hour
            var lastDay: Int = start.date.day
            tickerFlow(period = 1.seconds).map { currentTime() }.collect { now ->
                _hourMinute.value = now.hour to now.minute
                if (state.value.appSettings.timeConfig.dotsFlashEnabled) {
                    _dotsOn.value = !_dotsOn.value
                }
                if (now.minute != lastMinute) {
                    lastMinute = now.minute
                    // Календарь: пересчёт при смене дня (данные месяца —
                    // при смене месяца тем же условием через фильтр)
                    if (now.date.day != lastDay) {
                        lastDay = now.date.day
                        refreshCalendarData()
                    }
                }
                if (now.hour != lastHour) {
                    lastHour = now.hour
                    if (settingsUseCase.isHourlyBeepAllowed(currentHour = now.hour)) {
                        _hourlyBeep.tryEmit(Unit)
                    }
                }
            }
        }
    }

    // dispose не нужен: единый скоуп вьюмодели отменяет всё в onDispose

    private fun currentTime(): LocalDateTime = LocalDateTime.now().atStartOfSecond()
}
