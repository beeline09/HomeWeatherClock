package ru.weatherclock.adg.app.presentation.screens.settings

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.weatherclock.adg.app.domain.model.WeatherSettings
import ru.weatherclock.adg.app.presentation.components.viewModel.ViewModelState
import ru.weatherclock.adg.platformSpecific.weatherSettingsKStore

class SettingsScreenViewModel:
    ViewModelState<SettingsScreenState, SettingsScreenIntent>(SettingsScreenState(WeatherSettings())) {

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
        settingsJob = weatherSettingsKStore.updates.onEach {
            if (it != null) {
                setState {
                    SettingsScreenState(settings = it)
                }
            }
        }.launchIn(safeScope)
    }

    override fun dispose() {
        settingsJob?.cancel()
    }

}