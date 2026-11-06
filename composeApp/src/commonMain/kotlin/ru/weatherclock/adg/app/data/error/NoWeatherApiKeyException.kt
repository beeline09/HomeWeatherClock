package ru.weatherclock.adg.app.data.error

/**
 * Наследует Exception (а не Throwable): ForecastUseCase ловит
 * catch (e: Exception) — как Throwable он обходил этот catch и улетал
 * в глобальный обработчик до того, как сработает фолбэк на данные из БД.
 */
class NoWeatherApiKeyException : Exception() {
    override val message: String
        get() = "No weather API keys configured"
}
