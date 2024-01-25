package ru.weatherclock.adg.app.presentation.screens.settings

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.weatherclock.adg.app.domain.usecase.SettingsUseCase
import ru.weatherclock.adg.app.presentation.components.viewModel.ViewModelState

class SettingsScreenViewModel(
    settingsUseCase: SettingsUseCase
): ViewModelState<SettingsScreenState, SettingsScreenIntent>(SettingsScreenState()) {

    private var settingsJob: Job? = null

    override fun intent(intent: SettingsScreenIntent) {
        when (intent) {
            is SettingsScreenIntent.SaveProdCalendarData -> TODO()
            is SettingsScreenIntent.SaveWeatherApiKeys -> TODO()
            is SettingsScreenIntent.SaveWeatherKey -> TODO()
            is SettingsScreenIntent.SaveWeatherLanguageCode -> TODO()
        }
    }

    init {
        settingsJob?.cancel()
        settingsJob = settingsUseCase.getSettingsFlow().onEach {
            setState {
                SettingsScreenState(settings = it)
            }
        }.launchIn(safeScope)
    }

    override fun dispose() {
        settingsJob?.cancel()
    }

}