package com.ivzb.urgat.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Combines the most recent values of [Async] flows and calls [transform] with their data
 * once all of them are [Async.Success]. Otherwise emits [Async.Loading] if any of them is
 * loading, or [Async.Error] if any of them has failed, without terminating the flow.
 */
fun <T1, T2, T3, T4, T5, R> combineAsync(
    flow1: Flow<Async<T1>>,
    flow2: Flow<Async<T2>>,
    flow3: Flow<Async<T3>>,
    flow4: Flow<Async<T4>>,
    flow5: Flow<Async<T5>>,
    transform: suspend (T1, T2, T3, T4, T5) -> R,
): Flow<Async<R>> = combine(flow1, flow2, flow3, flow4, flow5) { async1, async2, async3, async4, async5 ->
    whenAllSuccess(async1, async2, async3, async4, async5) {
        transform(async1.requireData(), async2.requireData(), async3.requireData(), async4.requireData(), async5.requireData())
    }
}

/**
 * @see combineAsync
 */
fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combineAsync(
    flow1: Flow<Async<T1>>,
    flow2: Flow<Async<T2>>,
    flow3: Flow<Async<T3>>,
    flow4: Flow<Async<T4>>,
    flow5: Flow<Async<T5>>,
    flow6: Flow<Async<T6>>,
    flow7: Flow<Async<T7>>,
    flow8: Flow<Async<T8>>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
): Flow<Async<R>> = com.ivzb.urgat.util.combine(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8) { async1, async2, async3, async4, async5, async6, async7, async8 ->
    whenAllSuccess(async1, async2, async3, async4, async5, async6, async7, async8) {
        transform(
            async1.requireData(),
            async2.requireData(),
            async3.requireData(),
            async4.requireData(),
            async5.requireData(),
            async6.requireData(),
            async7.requireData(),
            async8.requireData(),
        )
    }
}

private inline fun <R> whenAllSuccess(vararg asyncs: Async<*>, onSuccess: () -> R): Async<R> = when {
    asyncs.any { it is Async.Loading } -> Async.Loading
    asyncs.any { it is Async.Error } -> Async.Error
    else -> Async.Success(onSuccess())
}

private fun <T> Async<T>.requireData(): T = (this as Async.Success<T>).data
