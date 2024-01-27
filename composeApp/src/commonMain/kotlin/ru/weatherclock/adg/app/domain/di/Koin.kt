@file:OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalCoroutinesApi::class
)

package ru.weatherclock.adg.app.domain.di

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.json.Json
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
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import ru.weatherclock.adg.app.data.remote.AppHttpClient
import ru.weatherclock.adg.app.data.remote.CalendarKtorService
import ru.weatherclock.adg.app.data.remote.WeatherKtorService
import ru.weatherclock.adg.app.data.remote.implementation.CalendarKtorServiceImpl
import ru.weatherclock.adg.app.data.remote.implementation.WeatherKtorServiceImpl
import ru.weatherclock.adg.app.data.repository.calendar.CalendarRemoteRepository
import ru.weatherclock.adg.app.data.repository.calendar.implementation.CalendarRemoteRepositoryImpl
import ru.weatherclock.adg.app.data.repository.db.forecast.ForecastDbRepository
import ru.weatherclock.adg.app.data.repository.db.forecast.implementation.ForecastDbRepositoryImpl
import ru.weatherclock.adg.app.data.repository.db.prodCalendar.ProdCalendarDbRepository
import ru.weatherclock.adg.app.data.repository.db.prodCalendar.implementation.ProdCalendarDbRepositoryImpl
import ru.weatherclock.adg.app.data.repository.settings.CalendarSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.ProdCalendarSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.TimeSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.UiSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.WeatherSettingsRepository
import ru.weatherclock.adg.app.data.repository.settings.implementation.CalendarSettingsRepositoryImpl
import ru.weatherclock.adg.app.data.repository.settings.implementation.ProdCalendarSettingsRepositoryImpl
import ru.weatherclock.adg.app.data.repository.settings.implementation.TimeSettingsRepositoryImpl
import ru.weatherclock.adg.app.data.repository.settings.implementation.UiSettingsRepositoryImpl
import ru.weatherclock.adg.app.data.repository.settings.implementation.WeatherSettingsRepositoryImpl
import ru.weatherclock.adg.app.data.repository.weather.WeatherRepository
import ru.weatherclock.adg.app.data.repository.weather.implementation.WeatherRepositoryImpl
import ru.weatherclock.adg.app.domain.model.settings.AppSettings
import ru.weatherclock.adg.app.domain.usecase.CalendarUseCase
import ru.weatherclock.adg.app.domain.usecase.ForecastUseCase
import ru.weatherclock.adg.app.domain.usecase.SettingsUseCase
import ru.weatherclock.adg.app.presentation.screens.home.HomeScreenViewModel
import ru.weatherclock.adg.app.presentation.screens.settings.SettingsScreenViewModel
import ru.weatherclock.adg.db.Database
import ru.weatherclock.adg.platformSpecific.appSettingsKStore
import ru.weatherclock.adg.platformSpecific.createDatabase
import ru.weatherclock.adg.platformSpecific.platformModule

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
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }
    single<CalendarRemoteRepository> { CalendarRemoteRepositoryImpl(get()) }
    single<ForecastDbRepository> { ForecastDbRepositoryImpl(get()) }

    single<WeatherKtorService> {
        WeatherKtorServiceImpl(get())
    }

    single<CalendarKtorService> { CalendarKtorServiceImpl(get()) }

    single { createJson() }

    single {
        createHttpClient(
            httpClientEngine = get(),
            json = get(),
            enableNetworkLogs = enableNetworkLogs
        )
    }

    single {
        AppHttpClient(get())
    }

    single<ProdCalendarDbRepository> { ProdCalendarDbRepositoryImpl(get()) }

    single<Database> { createDatabase() }
}

fun settingsRepoModule() = module {
    single<KStore<AppSettings>> { appSettingsKStore }
    single<CalendarSettingsRepository> { CalendarSettingsRepositoryImpl(get()) }
    single<UiSettingsRepository> { UiSettingsRepositoryImpl(get()) }
    single<ProdCalendarSettingsRepository> { ProdCalendarSettingsRepositoryImpl(get()) }
    single<TimeSettingsRepository> { TimeSettingsRepositoryImpl(get()) }
    single<WeatherSettingsRepository> { WeatherSettingsRepositoryImpl(get()) }
}

fun getUseCaseModule() = module {
    single {
        ForecastUseCase(
            get(),
            get(),
            get()
        )
    }
    single {
        CalendarUseCase(
            get(),
            get(),
            get()
        )
    }
    single {
        SettingsUseCase(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}

fun createHttpClient(
    httpClientEngine: HttpClientEngine,
    json: Json,
    enableNetworkLogs: Boolean
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
