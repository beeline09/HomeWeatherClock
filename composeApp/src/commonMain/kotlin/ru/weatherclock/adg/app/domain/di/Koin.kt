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
import ru.weatherclock.adg.app.data.remote.AbstractKtorService
import ru.weatherclock.adg.app.data.remote.KtorService
import ru.weatherclock.adg.app.data.repository.AbstractRepository
import ru.weatherclock.adg.app.data.repository.Repository
import ru.weatherclock.adg.app.domain.usecase.GetForecastUseCase
import ru.weatherclock.adg.app.presentation.screens.home.HomeScreenViewModel
import ru.weatherclock.adg.platformSpecific.platformModule

fun initKoin(
    enableNetworkLogs: Boolean = false,
    baseUrl: String,
    appDeclaration: KoinAppDeclaration = {},
) = startKoin {
    appDeclaration()
    modules(
        commonModule(
            enableNetworkLogs = enableNetworkLogs,
            baseUrl
        )
    )
}

// called by iOS etc
fun initKoin(baseUrl: String) = initKoin(
    enableNetworkLogs = true,
    baseUrl = baseUrl
) {}

fun commonModule(
    enableNetworkLogs: Boolean,
    baseUrl: String,
) =
    platformModule() +
            getDataModule(
                enableNetworkLogs,
                baseUrl
            ) +
            getUseCaseModule() +
            getScreenModelModule()

fun getScreenModelModule() = module {
    single { HomeScreenViewModel(get()) }
}

fun getDataModule(
    enableNetworkLogs: Boolean,
    baseUrl: String,
) = module {
    single<AbstractRepository> { Repository(get()) }

    single<AbstractKtorService> {
        KtorService(
            get(),
            baseUrl = baseUrl
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
    single { GetForecastUseCase(get()) }

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
