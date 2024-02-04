package ru.weatherclock.adg.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import ru.weatherclock.adg.app.data.remote.query.base.ApiQuery

class AppHttpClient(private val httpClient: HttpClient) {

    suspend fun <Q: ApiQuery> get(
        query: Q,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse = httpClient.get { url(query.buildUrl()); block() }

    suspend fun <Q: ApiQuery> post(
        query: Q,
        block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse = httpClient.post { url(query.buildUrl()); block() }
}