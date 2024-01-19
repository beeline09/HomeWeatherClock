package ru.weatherclock.adg.app.presentation.components.viewModel

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import cafe.adriel.voyager.core.model.ScreenModel

abstract class ViewModelState<STATE: State, INTENT: Intent>(
    initialState: STATE
): ScreenModel {

    protected val viewModelScope: CoroutineScope = object: CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = SupervisorJob() + Dispatchers.IO
    }

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(initialState)
    val state: StateFlow<STATE> get() = _state

    abstract fun intent(intent: INTENT)

    protected fun setState(block: STATE.() -> STATE) = viewModelScope.launch {
        val currentState = _state.value
        val newState = block.invoke(currentState)
        _state.tryEmit(newState)
    }
}