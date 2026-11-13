package ru.weatherclock.adg.app.presentation.components.viewModel

import cafe.adriel.voyager.core.model.ScreenModel
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
import kotlinx.coroutines.flow.update
import kotlin.coroutines.CoroutineContext

/**
 * Базовая вьюмодель экрана.
 *
 * Скоуп создаётся РОВНО ОДИН раз на экземпляр (раньше контекст был геттером
 * и клепал новый SupervisorJob на каждое обращение — из-за этого cancel()
 * в onDispose не отменял работающие корутины). Теперь все job'ы — дети
 * одного SupervisorJob и умирают вместе со смертью вьюмодели.
 *
 * SupervisorJob здесь нужен осознанно: исключение в одной корутине (сетевой
 * запрос, парсинг) не должно отменять соседние (секундный тикер часов).
 */
abstract class ViewModelState<STATE : State, INTENT : Intent>(
    initialState: STATE
) : ScreenModel {

    private val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO
    private val viewModelScope: CoroutineScope = CoroutineScope(coroutineContext)

    private val errorHandling = CoroutineExceptionHandler { _, throwable ->
        throwableState.tryEmit(throwable)
    }

    /** Единый скоуп вьюмодели: ошибки перехватываются в [catch]. */
    protected val safeScope: CoroutineScope = viewModelScope + errorHandling

    /** Ошибка, пойманная CoroutineExceptionHandler'ом (для тостов). */
    private val throwableState: MutableStateFlow<Throwable?> = MutableStateFlow(null)

    fun catch(block: (Throwable) -> Unit) = safeScope.launch {
        throwableState.filterNotNull().collectLatest { block.invoke(it) }
    }

    // Наружу — read-only StateFlow (private set); мутация — только через
    // [setState]. Единственный каст к MutableStateFlow инкапсулирован здесь.
    // (Explicit backing fields в Kotlin 2.2.21 всё ещё experimental —
    // в замороженный тулчейн не включаем)
    var state: StateFlow<STATE> = MutableStateFlow(initialState)
        private set

    abstract fun intent(intent: INTENT)

    protected open fun dispose() {}

    /** Атомарное обновление стейта (раньше read-then-emit терял апдейты). */
    protected fun setState(mutate: STATE.() -> STATE) {
        (state as MutableStateFlow<STATE>).update { it.mutate() }
    }

    final override fun onDispose() {
        viewModelScope.cancel()
        dispose()
    }
}
