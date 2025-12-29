@file:Suppress("UNCHECKED_CAST")

package com.ivzb.urgat.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

fun <T1, R> combineUiState(
    flow1: Flow<Async<T1>>,
    onLoading: () -> R,
    onError: () -> R,
    onSuccess: (T1) -> R,
): Flow<R> = combineUiState(
    // dummy flow is needed because combine requires 2 params
    arrayOf(flow1, MutableStateFlow(Async.Success(Unit))),
    onLoading,
    onError
) { data -> onSuccess(data[0] as T1) }

fun <T1, T2, R> combineUiState(
    flow1: Flow<Async<T1>>,
    flow2: Flow<Async<T2>>,
    onLoading: () -> R,
    onError: () -> R,
    onSuccess: (T1, T2) -> R,
): Flow<R> = combineUiState(
    arrayOf(flow1, flow2),
    onLoading,
    onError
) { data -> onSuccess(data[0] as T1, data[1] as T2) }

fun <T1, T2, T3, R> combineUiState(
    flow1: Flow<Async<T1>>,
    flow2: Flow<Async<T2>>,
    flow3: Flow<Async<T3>>,
    onLoading: () -> R,
    onError: () -> R,
    onSuccess: (T1, T2, T3) -> R,
): Flow<R> = combineUiState(
    arrayOf(flow1, flow2, flow3),
    onLoading,
    onError
) { data -> onSuccess(data[0] as T1, data[1] as T2, data[2] as T3) }

fun <T1, T2, T3, T4, R> combineUiState(
    flow1: Flow<Async<T1>>,
    flow2: Flow<Async<T2>>,
    flow3: Flow<Async<T3>>,
    flow4: Flow<Async<T4>>,
    onLoading: () -> R,
    onError: () -> R,
    onSuccess: (T1, T2, T3, T4) -> R,
): Flow<R> = combineUiState(
    arrayOf(flow1, flow2, flow3, flow4),
    onLoading,
    onError
) { data -> onSuccess(data[0] as T1, data[1] as T2, data[2] as T3, data[3] as T4) }

fun <T1, T2, T3, T4, T5, R> combineUiState(
    flow1: Flow<Async<T1>>,
    flow2: Flow<Async<T2>>,
    flow3: Flow<Async<T3>>,
    flow4: Flow<Async<T4>>,
    flow5: Flow<Async<T5>>,
    onLoading: () -> R,
    onError: () -> R,
    onSuccess: (T1, T2, T3, T4, T5) -> R,
): Flow<R> = combineUiState(
    arrayOf(flow1, flow2, flow3, flow4, flow5),
    onLoading,
    onError
) { data -> onSuccess(data[0] as T1, data[1] as T2, data[2] as T3, data[3] as T4, data[4] as T5) }

fun <T1, T2, T3, T4, T5, T6, R> combineUiState(
    flow1: Flow<Async<T1>>,
    flow2: Flow<Async<T2>>,
    flow3: Flow<Async<T3>>,
    flow4: Flow<Async<T4>>,
    flow5: Flow<Async<T5>>,
    flow6: Flow<Async<T6>>,
    onLoading: () -> R,
    onError: () -> R,
    onSuccess: (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> = combineUiState(
    arrayOf(flow1, flow2, flow3, flow4, flow5, flow6),
    onLoading,
    onError
) { data -> onSuccess(data[0] as T1, data[1] as T2, data[2] as T3, data[3] as T4, data[4] as T5, data[5] as T6) }


@Suppress("UNCHECKED_CAST")
private fun <R> combineUiState(
    flows: Array<Flow<*>>,
    onLoading: () -> R,
    onError: () -> R,
    onSuccess: (Array<Any?>) -> R
): Flow<R> = combine(*flows) { asyncItems ->
    when {
        asyncItems.isLoading() -> onLoading()
        asyncItems.isError() -> onError()
        asyncItems.isSuccess() -> onSuccess(asyncItems.data())
        else -> {
            Assert.fail("undefined state: $asyncItems")
            onError()
        }
    }
}

private fun Array<Any?>.isLoading() = any { it is Async.Loading }
private fun Array<Any?>.isError() = any { it is Async.Error }
private fun Array<Any?>.isSuccess() = all { it is Async.Success<*> }
private fun Array<Any?>.data() = map { (it as Async.Success<*>).data }.toTypedArray()
