package com.practicum.playlistmaker.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Debouncer<T>(
    private val delayMillis: Long,
    private val coroutineScope: CoroutineScope,
    private val useLastParam: Boolean,
    private val action: (T) -> Unit
) {
    private var debounceJob: Job? = null

    fun invoke(param: T) {
        if (useLastParam) {
            debounceJob?.cancel()
        }
        if (debounceJob?.isCompleted != false || useLastParam) {
            debounceJob = coroutineScope.launch {
                delay(delayMillis)
                action(param)
            }
        }
    }

    fun cancel() {
        debounceJob?.cancel()
        debounceJob = null
    }
}