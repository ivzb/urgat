package com.ivzb.urgat.ui.components

import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UiEventHolder<T> {

    private val uiEventFlow = MutableStateFlow<Async<T?>>(Async.Success(null))

    operator fun invoke(): StateFlow<Async<T?>> = uiEventFlow

    suspend fun emit(event: T?) {
        uiEventFlow.emit(Async.Success(event))
    }

}
