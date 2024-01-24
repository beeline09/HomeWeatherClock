package ru.weatherclock.adg.app.presentation.components.viewModel

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import cafe.adriel.voyager.core.model.ScreenModel
import ru.weatherclock.adg.app.domain.model.AppSettings
import ru.weatherclock.adg.app.domain.model.orDefault
import ru.weatherclock.adg.platformSpecific.appSettingsKStore

abstract class ViewModelState<STATE: State, INTENT: Intent>(
    initialState: STATE
): ScreenModel {

    private val viewModelScope: CoroutineScope = object: CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = SupervisorJob() + Dispatchers.IO
    }

    private val _throwableState: MutableStateFlow<Throwable?> = MutableStateFlow(null)

    private val errorHandling = CoroutineExceptionHandler { _, throwable ->
        _throwableState.tryEmit(throwable)
    }

    protected val safeScope: CoroutineScope = viewModelScope + errorHandling

    fun catch(block: (Throwable) -> Unit) = safeScope.launch {
        _throwableState
            .filterNotNull()
            .collectLatest { block.invoke(it) }
    }

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(initialState)
    val state: StateFlow<STATE> get() = _state

    abstract fun intent(intent: INTENT)

    protected open fun dispose() {}

    protected fun setState(block: STATE.() -> STATE) = safeScope.launch {
        val currentState = _state.value
        val newState = block.invoke(currentState)
        _state.tryEmit(newState)
    }

    protected fun updateSettings(block: AppSettings.() -> AppSettings) = safeScope.launch {
        val currentState = appSettingsKStore.get().orDefault()
        val newState = block.invoke(currentState)
        appSettingsKStore.update { newState }
    }

    final override fun onDispose() {
        viewModelScope.cancel()
        safeScope.cancel()
        super.onDispose()
        dispose()
    }
}