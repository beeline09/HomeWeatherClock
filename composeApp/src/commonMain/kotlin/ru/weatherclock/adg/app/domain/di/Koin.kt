package ru.weatherclock.adg.app.domain.di

import kotlinx.serialization.json.Json
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
import ru.weatherclock.adg.app.data.remote.CalendarKtorService
import ru.weatherclock.adg.app.data.remote.WeatherKtorService
import ru.weatherclock.adg.app.data.remote.implementation.CalendarKtorServiceImpl
import ru.weatherclock.adg.app.data.remote.implementation.WeatherKtorServiceImpl
import ru.weatherclock.adg.app.data.repository.CalendarRepository
import ru.weatherclock.adg.app.data.repository.WeatherRepository
import ru.weatherclock.adg.app.data.repository.implementation.CalendarRepositoryImpl
import ru.weatherclock.adg.app.data.repository.implementation.WeatherRepositoryImpl
import ru.weatherclock.adg.app.domain.usecase.CalendarUseCase
import ru.weatherclock.adg.app.domain.usecase.ForecastUseCase
import ru.weatherclock.adg.app.presentation.screens.home.HomeScreenViewModel
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
    enableNetworkLogs = true
) {}

fun commonModule(
    enableNetworkLogs: Boolean,
) =
    platformModule() +
            getDataModule(enableNetworkLogs) +
            getUseCaseModule() +
            getScreenModelModule()

fun getScreenModelModule() = module {
    single {
        HomeScreenViewModel(
            get(),
            get()
        )
    }
}

fun getDataModule(
    enableNetworkLogs: Boolean
) = module {
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }
    single<CalendarRepository> { CalendarRepositoryImpl(get()) }

    single<WeatherKtorService> {
        WeatherKtorServiceImpl(
            get(),
            baseUrl = "http://dataservice.accuweather.com"
        )
    }

    single<CalendarKtorService> {
        CalendarKtorServiceImpl(
            get(),
            baseUrl = "https://production-calendar.ru/get/ru"
        )
    }

    single { createJson() }

    single {
        createHttpClient(
            get(),
            get(),
            enableNetworkLogs = enableNetworkLogs
        )
    }
}

fun getUseCaseModule() = module {
    single { ForecastUseCase(get()) }
    single { CalendarUseCase(get()) }
}

fun createHttpClient(
    httpClientEngine: HttpClientEngine,
    json: Json,
    enableNetworkLogs: Boolean
) =
    HttpClient(httpClientEngine) {
//        defaultRequest { header("X-Yandex-API-Key", "28c78432-9cb7-4b4e-8a08-c7679a11022c") }

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
