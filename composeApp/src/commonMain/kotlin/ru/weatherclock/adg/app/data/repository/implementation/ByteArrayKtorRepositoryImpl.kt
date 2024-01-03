package ru.weatherclock.adg.app.data.repository.implementation

import ru.weatherclock.adg.app.data.remote.ByteArrayKtorService
import ru.weatherclock.adg.app.data.repository.ByteArrayKtorRepository

class ByteArrayKtorRepositoryImpl(private val service: ByteArrayKtorService):
    ByteArrayKtorRepository() {

    override suspend fun getUrlAsInputStream(url: String): ByteArray {
        return service.getUrlAsInputStream(url)
    }
}