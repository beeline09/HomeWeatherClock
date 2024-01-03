package ru.weatherclock.adg.app.data.repository

abstract class ByteArrayKtorRepository {

    abstract suspend fun getUrlAsInputStream(
        url: String
    ): ByteArray
}
