package ru.weatherclock.adg.app.presentation.screens.settings

import ru.weatherclock.adg.app.presentation.components.viewModel.Intent

sealed interface SettingsScreenIntent: Intent {

    data class SaveWeatherKey(val key: String): SettingsScreenIntent {
        init {
            if (key.isBlank()) throw IllegalArgumentException("Key cannot be empty!")
        }
    }

    data class SaveWeatherLanguageCode(val code: String): SettingsScreenIntent {
        init {
            if (code.isBlank()) throw IllegalArgumentException("Language code cannot be empty!")
        }
    }

    data class SaveWeatherApiKeys(val apiKey: String): SettingsScreenIntent {
        init {
            if (apiKey.isBlank()) throw IllegalArgumentException("API key cannot be empty!")
        }
    }

    data class SaveProdCalendarData(
        val isRussia: Boolean,
        val regionCode: Int
    ): SettingsScreenIntent {

        init {
            if (isRussia && regionCode <= 0) throw IllegalArgumentException("Region cannot be smaller than 1 for Russia!")
        }
    }
}