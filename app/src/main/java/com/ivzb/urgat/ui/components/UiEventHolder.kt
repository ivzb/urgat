package com.ivzb.urgat.ui.components

import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UiEventHolder<T> {

    private val _uiEvent = MutableStateFlow<Async<T?>>(Async.Success(null))

    operator fun invoke(): StateFlow<Async<T?>> = _uiEvent

    suspend fun emit(event: T?) {
        _uiEvent.emit(Async.Success(event))
    }

}