package ru.weatherclock.adg.app.presentation.components.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext

/**
 * Базовая вьюмодель экрана на androidx Lifecycle ViewModel (KMP).
 *
 * safeScope — дети встроенного viewModelScope (отменяется системой при
 * смерти вьюмодели), но на Dispatchers.IO: блокирующие запросы БД/сеть
 * не встают на Main. Один SupervisorJob на экземпляр: исключение в
 * одной корутине (сетевой запрос) не отменяет соседние (тикер часов).
 */
abstract class ViewModelState<STATE : State, INTENT : Intent>(
    initialState: STATE
) : ViewModel() {

    private val throwableState: MutableStateFlow<Throwable?> = MutableStateFlow(null)

    private val errorHandling = CoroutineExceptionHandler { _, throwable ->
        throwableState.tryEmit(throwable)
    }

    private val scopeContext: CoroutineContext =
        SupervisorJob(viewModelScope.coroutineContext[Job]) + Dispatchers.IO + errorHandling

    /** Единый скоуп вьюмодели: ошибки перехватываются в [catch]. */
    protected val safeScope: CoroutineScope = CoroutineScope(scopeContext)

    fun catch(block: (Throwable) -> Unit) = safeScope.launch {
        throwableState.filterNotNull().collectLatest { block.invoke(it) }
    }

    // Наружу — read-only StateFlow (private set); мутация — только через
    // [setState]. Единственный каст к MutableStateFlow инкапсулирован здесь
    var state: StateFlow<STATE> = MutableStateFlow(initialState)
        private set

    abstract fun intent(intent: INTENT)

    protected open fun dispose() {}

    /** Атомарное обновление стейта (read-then-emit терял параллельные апдейты). */
    protected fun setState(mutate: STATE.() -> STATE) {
        (state as MutableStateFlow<STATE>).update { it.mutate() }
    }

    override fun onCleared() {
        safeScope.cancel()
        dispose()
        super.onCleared()
    }
}
