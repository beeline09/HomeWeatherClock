package ru.weatherclock.adg.app.presentation.screens.home

import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import cafe.adriel.voyager.core.model.ScreenModel
import ru.weatherclock.adg.app.data.Result
import ru.weatherclock.adg.app.data.asResult
import ru.weatherclock.adg.app.domain.model.Forecast
import ru.weatherclock.adg.app.domain.usecase.CalendarUseCase
import ru.weatherclock.adg.app.domain.usecase.ForecastUseCase
import ru.weatherclock.adg.app.presentation.components.tickerFlow

class HomeScreenViewModel(
    private val forecastUseCase: ForecastUseCase,
    private val calendarUseCase: CalendarUseCase
): ScreenModel {

    private val job = SupervisorJob()
    private val coroutineContextX: CoroutineContext = job + Dispatchers.IO
    private val viewModelScope = CoroutineScope(coroutineContextX)
    private val timersScope = CoroutineScope(coroutineContextX)

    private val _forecast = MutableStateFlow<ForecastState>(ForecastState.Idle)
    val forecast = _forecast.asStateFlow()

    private val _dot = MutableStateFlow<String>(":")
    val dot = _dot.asStateFlow()

    private val _time: MutableStateFlow<Pair<String, String>> = MutableStateFlow("00" to "00")
    val time = _time.asStateFlow()

    private val _date = MutableStateFlow(
        Triple(
            0,
            0,
            0
        )
    )
    val date = _date.asStateFlow()

    init {
        tickerFlow(period = 1.seconds)
            .map { Clock.System.now() }
            .onEach {
                val dotStr = if (dot.value == " ") ":" else " "
                _dot.value = dotStr
            }
            .launchIn(timersScope)

        tickerFlow(period = 1.seconds)
            .map { Clock.System.now() }
            .distinctUntilChanged { old, new ->
                val oltTime = old.toLocalDateTime(TimeZone.currentSystemDefault())
                val newTime = new.toLocalDateTime(TimeZone.currentSystemDefault())
                oltTime.hour == newTime.hour && oltTime.minute == newTime.minute
            }
            .onEach {
                val ldt = it.toLocalDateTime(TimeZone.currentSystemDefault())
                val hour = ldt.hour
                val minute = ldt.minute
                val dayOfMonth = ldt.dayOfMonth
                val month = ldt.monthNumber
                val year = ldt.year
                _time.value = "$hour".padStart(
                    2,
                    '0'
                ) to "$minute".padStart(
                    2,
                    '0'
                )
                _date.value = Triple(
                    dayOfMonth,
                    month,
                    year
                )
            }
            .launchIn(timersScope)
    }

    fun onLaunch() {
//        getForecast()
        getProdCalendar()
    }

    override fun onDispose() {
        super.onDispose()
        timersScope.cancel()
    }

    private fun getProdCalendar() {
        val year = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
        calendarUseCase.invoke(period = "$year").onEach {
            println("Day count: ${it.size}")
        }.launchIn(viewModelScope)
    }

    private fun getForecast() {
        if (_forecast.value !is ForecastState.Success) {
            viewModelScope.launch(Dispatchers.IO) {
                forecastUseCase().asResult().collectLatest { result ->
                    when (result) {
                        is Result.Error -> {
                            _forecast.update { ForecastState.Error(result.exception) }
                        }

                        is Result.Loading -> {
                            _forecast.update { ForecastState.Loading }
                        }

                        is Result.Success -> {
                            _forecast.update { ForecastState.Success(result.data) }
                        }
                    }
                }
            }
        }

    }

    sealed interface ForecastState {

        data object Loading: ForecastState

        data object Idle: ForecastState

        data class Success(val movies: Forecast): ForecastState

        data class Error(val error: String): ForecastState
    }
}