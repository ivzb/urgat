package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.base.BaseRepository
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.util.Async
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Covers the shared observe pipeline in [DefaultObserveUseCase] through its concrete
 * [ObserveUseCase] subclass. Every `Observe*UseCase` in the app delegates to it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ObserveUseCaseTest {

    private lateinit var repository: BaseRepository<String>
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
    }

    @Test
    fun `invoke should use no search query no date range and no limit when flows are null`() = runTest(testDispatcher) {
        every { repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE) } returns flowOf(items)

        // the fallback flows are hot, so the stream never completes - take the first emission
        val result = useCase()(null, null, null, null, null).first()

        advanceUntilIdle()

        verify(exactly = 1) { repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE) }
        assertEquals(items, (result as Async.Success).data)
    }

    @Test
    fun `invoke should observe without search query when search query is blank`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(items)

        val results = useCase()(flowOf("   "), flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        verify(exactly = 1) { repository.observe(10, NO_DATE_RANGE) }
        verify(exactly = 0) { repository.observe(any(), any(), any()) }
        assertEquals(items, (results.first() as Async.Success).data)
    }

    @Test
    fun `invoke should observe with search query when search query is not blank`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE, "query") } returns flowOf(items)

        val results = useCase()(flowOf("query"), flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        verify(exactly = 1) { repository.observe(10, NO_DATE_RANGE, "query") }
        verify(exactly = 0) { repository.observe(any(), any()) }
        assertEquals(items, (results.first() as Async.Success).data)
    }

    @Test
    fun `invoke should forward the date range to the repository`() = runTest(testDispatcher) {
        val dateRange = LongRange(100L, 200L)
        every { repository.observe(10, dateRange) } returns flowOf(items)

        useCase()(flowOf(""), flowOf(dateRange), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        verify(exactly = 1) { repository.observe(10, dateRange) }
    }

    @Test
    fun `invoke should debounce rapid search query changes`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE, "abc") } returns flowOf(items)

        val searchQueryFlow = flow {
            emit("a")
            emit("ab")
            emit("abc")
        }

        useCase()(searchQueryFlow, flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        verify(exactly = 1) { repository.observe(10, NO_DATE_RANGE, "abc") }
        verify(exactly = 0) { repository.observe(10, NO_DATE_RANGE, "a") }
        verify(exactly = 0) { repository.observe(10, NO_DATE_RANGE, "ab") }
    }

    @Test
    fun `invoke should not filter when filter type flow is null`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(items)

        val results = useCase()(flowOf(""), flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        assertEquals(items, (results.first() as Async.Success).data)
    }

    @Test
    fun `invoke should filter when filter type flow is provided`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(items)

        val results = useCase()(flowOf(""), flowOf(NO_DATE_RANGE), flowOf("b"), null, flowOf(10)).toList()

        advanceUntilIdle()

        assertEquals(listOf("banana"), (results.first() as Async.Success).data)
    }

    @Test
    fun `invoke should not sort when sort type flow is null`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(items)

        val results = useCase()(flowOf(""), flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        assertEquals(items, (results.first() as Async.Success).data)
    }

    @Test
    fun `invoke should sort when sort type flow is provided`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(items)

        val results = useCase()(flowOf(""), flowOf(NO_DATE_RANGE), null, flowOf("desc"), flowOf(10)).toList()

        advanceUntilIdle()

        assertEquals(listOf("cherry", "banana", "apple"), (results.first() as Async.Success).data)
    }

    @Test
    fun `invoke should always apply transform`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(items)

        val useCase = ObserveUseCase<String, String, String>(
            repository = repository,
            transform = { list -> list.map { it.uppercase() } },
        )

        val results = useCase(flowOf(""), flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        assertEquals(listOf("APPLE", "BANANA", "CHERRY"), (results.first() as Async.Success).data)
    }

    @Test
    fun `invoke should apply filter then sort then transform in order`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(listOf("apple", "avocado", "banana"))

        val useCase = ObserveUseCase<String, String, String>(
            repository = repository,
            filter = { list, type -> list.filter { it.startsWith(type) } },
            sort = { list, type -> if (type == "asc") list.sorted() else list.sortedDescending() },
            transform = { list -> list.map { it.uppercase() } },
        )

        val results = useCase(flowOf(""), flowOf(NO_DATE_RANGE), flowOf("a"), flowOf("desc"), flowOf(10)).toList()

        advanceUntilIdle()

        assertEquals(listOf("AVOCADO", "APPLE"), (results.first() as Async.Success).data)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } throws RuntimeException("Test error")

        val results = useCase()(flowOf(""), flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        assert(results.first() is Async.Error)
    }

    @Test
    fun `invoke should return Error when repository stream throws`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flow { throw RuntimeException("Test error") }

        val results = useCase()(flowOf(""), flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        assert(results.first() is Async.Error)
    }

    private fun useCase() = ObserveUseCase<String, String, String>(
        repository = repository,
        filter = { list, type -> list.filter { it.startsWith(type) } },
        sort = { list, type -> if (type == "asc") list.sorted() else list.sortedDescending() },
    )

    companion object {

        private val items = listOf("apple", "banana", "cherry")

    }

}



