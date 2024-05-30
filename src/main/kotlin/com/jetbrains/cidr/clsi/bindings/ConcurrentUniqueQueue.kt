package com.jetbrains.cidr.clsi.bindings

import kotlinx.coroutines.channels.Channel

class ConcurrentUniqueQueue<T> {
    private val buffer: MutableList<T> = mutableListOf()
    private val signal: Channel<Unit> = Channel(0)

    fun send(value: T) {
        val notify = synchronized(buffer) {
            if (buffer.contains(value)) {
                false
            } else {
                buffer.add(value)
            }
        }

        if (notify) {
            signal.trySend(Unit)
        }
    }

    suspend fun current(): T {
        while (true) {
            val result = synchronized(buffer) {
                buffer.firstOrNull()
            }
            if (result != null) return result

            signal.receive()
        }
    }

    fun remove() {
       synchronized(buffer) {
           if (buffer.isNotEmpty()) buffer.removeAt(0)
       }
    }
}