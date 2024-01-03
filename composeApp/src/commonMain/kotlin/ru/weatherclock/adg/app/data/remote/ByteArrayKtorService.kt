package ru.weatherclock.adg.app.data.remote

abstract class ByteArrayKtorService {

    abstract suspend fun getUrlAsInputStream(
        url: String
    ): ByteArray
}
