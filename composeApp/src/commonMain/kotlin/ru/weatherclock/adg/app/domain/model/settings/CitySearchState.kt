package ru.weatherclock.adg.app.domain.model.settings

import ru.weatherclock.adg.app.data.dto.CityConfig

/**
 * Состояние автопоиска населённого пункта в настройках.
 */
sealed interface CitySearchState {

    /** Нет активного запроса (меньше 3 символов). */
    data object Idle : CitySearchState

    /** Запрос выполняется (показывается спиннер). */
    data object Loading : CitySearchState

    /** Поиск успешно вернул список (возможно, пустой — «не найден»). */
    data class Results(val cities: List<CityConfig>) : CitySearchState

    /** Запрос не удался (сеть/лимиты/ключ) — показывается текст ошибки. */
    data class Error(val cause: Throwable? = null) : CitySearchState
}
