package ru.weatherclock.adg.app.presentation.screens.home

import ru.weatherclock.adg.app.presentation.components.viewModel.Intent

sealed interface HomeScreenIntent: Intent {

    sealed interface Settings: HomeScreenIntent {
        data object Show: Settings
        data object Hide: Settings
    }

}