package ru.weatherclock.adg.app.domain.usecase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import ru.weatherclock.adg.app.data.repository.ByteArrayKtorRepository

class ByteArrayUseCase(private val repository: ByteArrayKtorRepository) {

    private val scope = CoroutineScope(Dispatchers.IO)

    fun readUrlAsInputStream(
        url: String,
        onSuccess: (ByteArray) -> Unit
    ) {
        scope.launch {
            val bytes = repository.getUrlAsInputStream(url)
            launch(Dispatchers.Default) {
                onSuccess(bytes)
            }
        }
    }
}
