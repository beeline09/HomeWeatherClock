package ru.weatherclock.adg.app.presentation.screens.home

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.domain.model.calendar.ProdCalendarDay
import ru.weatherclock.adg.app.domain.util.now
import ru.weatherclock.adg.app.presentation.components.viewModel.State

/**
 * Только МЕДЛЕННО меняющиеся данные (день/настройки/кнопка): рекомпозиции
 * главного экрана происходят при реальном изменении. Быстрые величины
 * (часы:минуты, мигание точек, прогноз) — отдельные StateFlow вьюмодели.
 */
data class HomeScreenState(
    val date: LocalDate = LocalDateTime.now().date, // now — domain.util
    val currentProdDay: ProdCalendarDay? = null,
    val prodCalendarDaysForCurrentMonth: List<ProdCalendarDay> = emptyList(),
    val settingsButtonShowed: Boolean = false,
    val appSettings: AppSettings = AppSettings()
): State
