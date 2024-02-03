package ru.weatherclock.adg.app.data.util

import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

class CircularIterator<T>(val list: List<T>): Iterator<T> {

    private val ai: AtomicInt = atomic(0)

    override fun hasNext(): Boolean {
        return list.isNotEmpty()
    }

    override fun next(): T {
        val nextIndex = ai.getAndIncrement()
        return if (nextIndex < list.size) {
            list[nextIndex]
        } else {
            ai.update { 1 }
            list.first()
        }
    }

}