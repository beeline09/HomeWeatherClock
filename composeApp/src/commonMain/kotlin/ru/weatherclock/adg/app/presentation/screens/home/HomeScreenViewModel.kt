package ru.weatherclock.adg.app.presentation.screens.home

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import cafe.adriel.voyager.core.model.ScreenModel
import ru.weatherclock.adg.app.data.Result
import ru.weatherclock.adg.app.data.asResult
import ru.weatherclock.adg.app.domain.model.Forecast
import ru.weatherclock.adg.app.domain.usecase.GetForecastUseCase

class HomeScreenViewModel(private val forecastUseCase: GetForecastUseCase): ScreenModel {

    private val job = SupervisorJob()
    private val coroutineContextX: CoroutineContext = job + Dispatchers.IO
    private val viewModelScope = CoroutineScope(coroutineContextX)

    private val _forecast = MutableStateFlow<ForecastState>(ForecastState.Idle)
    val forecast = _forecast.asStateFlow()

    fun onLaunch() {
        getForecast()
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