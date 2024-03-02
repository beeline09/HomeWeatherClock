package ru.weatherclock.adg.app.domain.di

import io.github.xxfast.kstore.KStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import ru.weatherclock.adg.app.data.dto.AppSettings
import ru.weatherclock.adg.app.data.remote.AppHttpClient
import ru.weatherclock.adg.app.data.remote.service.CalendarKtorService
import ru.weatherclock.adg.app.data.remote.service.implementation.AccuweatherKtorServiceImpl
import ru.weatherclock.adg.app.data.remote.service.implementation.CalendarKtorServiceImpl
import ru.weatherclock.adg.app.data.remote.service.implementation.OpenWeatherMapKtorServiceImpl
import ru.weatherclock.adg.app.data.repository.calendar.CalendarRemoteRepository
import ru.weatherclock.adg.app.data.repository.calendar.implementation.CalendarRemoteRepositoryImpl
import ru.weatherclock.adg.app.data.repository.db.forecast.AccuweatherDbRepository
import ru.weatherclock.adg.app.data.repository.db.forecast.OpenWeatherMapDbRepository
import ru.weatherclock.adg.app.data.repository.db.forecast.implementation.AccuweatherDbRepositoryImpl
import ru.weatherclock.adg.app.data.repository.db.forecast.implementation.OpenWeatherMapDbRepositoryImpl
import ru.weatherclock.adg.app.data.repository.db.prodCalendar.ProdCalendarDbRepository
import ru.weatherclock.adg.app.data.repository.db.prodCalendar.implementation.ProdCalendarDbRepositoryImpl
import ru.weatherclock.adg.app.data.repository.settings.AllSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.CalendarSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.ProdCalendarSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.TimeSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.UiSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.WeatherSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.implementation.AllSettingsRepositoryImpl
import ru.weatherclock.adg.app.data.repository.settings.implementation.CalendarSettingsRepositoryImpl
import ru.weatherclock.adg.app.data.repository.settings.implementation.ProdCalendarSettingsRepositoryImpl
import ru.weatherclock.adg.app.data.repository.settings.implementation.TimeSettingsRepositoryImpl
import ru.weatherclock.adg.app.data.repository.settings.implementation.UiSettingsRepositoryImpl
import ru.weatherclock.adg.app.data.repository.settings.implementation.WeatherSettingsRepositoryImpl
import ru.weatherclock.adg.app.data.repository.weather.implementation.AccuweatherForecastRepositoryImpl
import ru.weatherclock.adg.app.data.repository.weather.implementation.OpenWeatherMapForecastRepositoryImpl
import ru.weatherclock.adg.app.domain.usecase.CalendarUseCase
import ru.weatherclock.adg.app.domain.usecase.ForecastUseCase
import ru.weatherclock.adg.app.domain.usecase.SettingsUseCase
import ru.weatherclock.adg.app.presentation.screens.home.HomeScreenViewModel
import ru.weatherclock.adg.app.presentation.screens.settings.SettingsScreenViewModel
import ru.weatherclock.adg.db.AccuweatherDb
import ru.weatherclock.adg.db.OpenWeatherMapDb
import ru.weatherclock.adg.db.ProdCalendarDb
import ru.weatherclock.adg.platformSpecific.DbHelper.createAccuweatherDb
import ru.weatherclock.adg.platformSpecific.DbHelper.createOpenWeatherMapDb
import ru.weatherclock.adg.platformSpecific.DbHelper.createProdCalendarDb
import ru.weatherclock.adg.platformSpecific.PlatformHelper.appSettings
import ru.weatherclock.adg.platformSpecific.PlatformHelper.defaultHttpClientEngine

fun initKoin(
    enableNetworkLogs: Boolean = false,
    appDeclaration: KoinAppDeclaration = {},
) = startKoin {
    appDeclaration()
    modules(
        commonModule(
            enableNetworkLogs = enableNetworkLogs
        )
    )
}

// called by iOS etc
fun initKoin() = initKoin(
    enableNetworkLogs = false
) {}

fun commonModule(
    enableNetworkLogs: Boolean,
) =
    platformModule() + settingsRepoModule() + getDataModule(enableNetworkLogs) + getUseCaseModule() + getScreenModelModule()

private fun platformModule() = module { single { defaultHttpClientEngine } }

fun getScreenModelModule() = module {
    single {
        HomeScreenViewModel(
            forecastUseCase = get(),
            calendarUseCase = get(),
            settingsUseCase = get(),
        )
    }
    single {
        SettingsScreenViewModel(get())
    }
}

fun getDataModule(
    enableNetworkLogs: Boolean
) = module {
    single<AccuweatherForecastRepositoryImpl> {
        AccuweatherForecastRepositoryImpl(
            ktorService = get()
        )
    }
    single<OpenWeatherMapForecastRepositoryImpl> {
        OpenWeatherMapForecastRepositoryImpl(
            ktorService = get()
        )
    }
    single<CalendarRemoteRepository> { CalendarRemoteRepositoryImpl(ktorService = get()) }
    single<AccuweatherDbRepository> { AccuweatherDbRepositoryImpl(database = get()) }
    single<OpenWeatherMapDbRepository> { OpenWeatherMapDbRepositoryImpl(database = get()) }

    single<AccuweatherKtorServiceImpl> {
        AccuweatherKtorServiceImpl(httpClient = get())
    }

    single<OpenWeatherMapKtorServiceImpl> {
        OpenWeatherMapKtorServiceImpl(httpClient = get())
    }

    single<CalendarKtorService> { CalendarKtorServiceImpl(httpClient = get()) }

    single { createJson() }

    single {
        createHttpClient(
            httpClientEngine = get(), json = get(), enableNetworkLogs = enableNetworkLogs
        )
    }

    single {
        AppHttpClient(httpClient = get())
    }

    single<ProdCalendarDbRepository> { ProdCalendarDbRepositoryImpl(get()) }

    single<ProdCalendarDb> { createProdCalendarDb() }
    single<AccuweatherDb> { createAccuweatherDb() }
    single<OpenWeatherMapDb> { createOpenWeatherMapDb() }
}

fun settingsRepoModule() = module {
    single<KStore<AppSettings>> { appSettings }
    single<CalendarSettingsRepository> { CalendarSettingsRepositoryImpl(get()) }
    single<UiSettingsRepository> { UiSettingsRepositoryImpl(get()) }
    single<ProdCalendarSettingsRepository> { ProdCalendarSettingsRepositoryImpl(get()) }
    single<TimeSettingsRepository> { TimeSettingsRepositoryImpl(get()) }
    single<WeatherSettingsRepository> { WeatherSettingsRepositoryImpl(get()) }
    single<AllSettingsRepository> { AllSettingsRepositoryImpl(get()) }
}

fun getUseCaseModule() = module {
    single {
        ForecastUseCase(
            accuweatherRepository = get(),
            accuweatherDbRepository = get(),
            openWeatherMapRepository = get(),
            weatherSettingsRepository = get(),
            openWeatherMapDbRepository = get()
        )
    }
    single {
        CalendarUseCase(
            get(), get(), get()
        )
    }
    single {
        SettingsUseCase(
            timeRepo = get(),
            calendarRepo = get(),
            prodCalendarRepo = get(),
            weatherRepo = get(),
            mainSettingsRepo = get(),
        )
    }
}

fun createHttpClient(
    httpClientEngine: HttpClientEngine, json: Json, enableNetworkLogs: Boolean
) = HttpClient(httpClientEngine) {

    // exception handling
    install(HttpTimeout) {
        requestTimeoutMillis = 10000
        connectTimeoutMillis = 10000
        socketTimeoutMillis = 10000
    }

    install(HttpRequestRetry) {
        maxRetries = 3
        retryIf { _, response -> !response.status.isSuccess() }
        retryOnExceptionIf { _, cause -> cause is HttpRequestTimeoutException }
        delayMillis { 3000L } // retries in 3, 6, 9, etc. seconds
    }

    install(HttpCallValidator) {
        handleResponseExceptionWithRequest { cause, _ -> println("exception $cause") }
    }

    install(ContentNegotiation) { json(json) }
    if (enableNetworkLogs) {
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }
}

fun createJson() = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
    prettyPrint = true
    coerceInputValues = true
}
