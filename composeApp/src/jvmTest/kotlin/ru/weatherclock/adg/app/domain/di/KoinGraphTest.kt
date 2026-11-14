package ru.weatherclock.adg.app.domain.di

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import ru.weatherclock.adg.app.data.repository.location.LocationRepository
import ru.weatherclock.adg.app.domain.usecase.CalendarUseCase
import ru.weatherclock.adg.app.domain.usecase.ForecastUseCase
import ru.weatherclock.adg.app.domain.usecase.SettingsUseCase
import ru.weatherclock.adg.app.presentation.screens.home.HomeScreenViewModel
import ru.weatherclock.adg.app.presentation.screens.settings.SettingsScreenViewModel
import kotlin.test.Test

/**
 * Живая проверка DI-графа: резолв всех ключевых зависимостей.
 *
 * Регресс ночного краша на планшете: SettingsUseCase стал ждать
 * LocationRepository, а регистрации остались по конкретным классам —
 * «No definition found» на старте. Тест резолвит зависимости РЕАЛЬНО
 * и падает в jvmTest до того, как APK увидит устройство.
 */
class KoinGraphTest {

    @Test
    fun `DI-граф резолвит все ключевые зависимости`() {
        startKoin {
            modules(
                commonModule(enableNetworkLogs = false)
            )
        }
        try {
            val koin = org.koin.core.context.GlobalContext.get()
            // use case'ы (внутри — репозитории, сервисы, БД, http-клиент)
            koin.get<ForecastUseCase>(ForecastUseCase::class)
            koin.get<CalendarUseCase>(CalendarUseCase::class)
            koin.get<SettingsUseCase>(SettingsUseCase::class)
            // вьюмодели (внутри — весь граф use case'ов)
            koin.get<HomeScreenViewModel>(HomeScreenViewModel::class)
            koin.get<SettingsScreenViewModel>(SettingsScreenViewModel::class)
            // именованные реализации одного интерфейса
            koin.get<LocationRepository>(LocationRepository::class, named("accuweather"))
            koin.get<LocationRepository>(LocationRepository::class, named("openWeatherMap"))
        } finally {
            stopKoin()
        }
    }
}
