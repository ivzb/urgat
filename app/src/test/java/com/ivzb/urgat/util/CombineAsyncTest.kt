package com.ivzb.urgat.util

import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CombineAsyncTest {

    @Test
    fun `combineAsync should emit Success with transformed data when all inputs succeed`() = runTest {
        val result = combineAsync(
            success(1),
            success(2),
            success(3),
            success(4),
            success(5),
        ) { a, b, c, d, e -> listOf(a, b, c, d, e) }.first()

        assertEquals(Async.Success(listOf(1, 2, 3, 4, 5)), result)
    }

    @Test
    fun `combineAsync should emit Error without calling transform when any input fails`() = runTest {
        var transformCalls = 0

        val result = combineAsync(
            success(1),
            flowOf<Async<Int>>(Async.Error),
            success(3),
            success(4),
            success(5),
        ) { _, _, _, _, _ -> transformCalls++ }.first()

        assertEquals(Async.Error, result)
        assertEquals(0, transformCalls)
    }

    @Test
    fun `combineAsync should emit Loading when any input is loading even if another fails`() = runTest {
        val result = combineAsync(
            success(1),
            flowOf<Async<Int>>(Async.Error),
            flowOf<Async<Int>>(Async.Loading),
            success(4),
            success(5),
        ) { a, b, c, d, e -> a + b + c + d + e }.first()

        assertEquals(Async.Loading, result)
    }

    @Test
    fun `combineAsync should keep emitting after an error once the input recovers`() = runTest {
        val flaky = MutableStateFlow<Async<Int>>(Async.Error)
        val results = mutableListOf<Async<Int>>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            combineAsync(
                flaky,
                success(2),
                success(3),
                success(4),
                success(5),
            ) { a, b, c, d, e -> a + b + c + d + e }.toList(results)
        }
        runCurrent()

        flaky.value = Async.Success(1)
        runCurrent()

        assertEquals(listOf(Async.Error, Async.Success(15)), results)
    }

    @Test
    fun `combineAsync with 8 flows should emit Success with transformed data when all inputs succeed`() = runTest {
        val result = combineAsync(
            success(1),
            success("2"),
            success(3L),
            success(4),
            success(5),
            success(6),
            success(7),
            success(8),
        ) { a, b, c, d, e, f, g, h -> listOf(a, b, c, d, e, f, g, h) }.first()

        assertEquals(Async.Success(listOf(1, "2", 3L, 4, 5, 6, 7, 8)), result)
    }

    @Test
    fun `combineAsync with 8 flows should emit Error when any input fails`() = runTest {
        val result = combineAsync(
            success(1),
            success(2),
            success(3),
            success(4),
            success(5),
            success(6),
            success(7),
            flowOf<Async<Int>>(Async.Error),
        ) { a, b, c, d, e, f, g, h -> a + b + c + d + e + f + g + h }.first()

        assertEquals(Async.Error, result)
    }

    private fun <T> success(data: T) = flowOf<Async<T>>(Async.Success(data))

}
