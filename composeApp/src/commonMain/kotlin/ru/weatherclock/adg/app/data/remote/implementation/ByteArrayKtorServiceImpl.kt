package ru.weatherclock.adg.app.data.remote.implementation

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import ru.weatherclock.adg.app.data.remote.ByteArrayKtorService

class ByteArrayKtorServiceImpl(private val httpClient: HttpClient): ByteArrayKtorService() {

    override suspend fun getUrlAsInputStream(url: String): ByteArray {
        return httpClient.get(urlString = url).readBytes()
    }
}