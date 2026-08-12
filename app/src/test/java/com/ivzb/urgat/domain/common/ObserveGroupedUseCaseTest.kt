package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.base.BaseRepository
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.util.Async
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
 * Covers [ObserveGroupedUseCase], the shared grouping pipeline that every
 * `ObserveGrouped*UseCase` in the app delegates to.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ObserveGroupedUseCaseTest {

    private lateinit var repository: BaseRepository<String>
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
    }

    @Test
    fun `invoke should return Success with the grouped result`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(items)

        val results = useCase()(flowOf(""), flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        assertEquals(
            mapOf("a" to listOf("apple", "avocado"), "b" to listOf("banana")),
            (results.first() as Async.Success).data,
        )
    }

    @Test
    fun `invoke should group after filtering and sorting`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(items)

        val results = useCase()(flowOf(""), flowOf(NO_DATE_RANGE), flowOf("a"), flowOf("desc"), flowOf(10)).toList()

        advanceUntilIdle()

        assertEquals(
            mapOf("a" to listOf("avocado", "apple")),
            (results.first() as Async.Success).data,
        )
    }

    @Test
    fun `invoke should observe with search query when search query is not blank`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE, "apple") } returns flowOf(listOf("apple"))

        val results = useCase()(flowOf("apple"), flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        verify(exactly = 1) { repository.observe(10, NO_DATE_RANGE, "apple") }
        assertEquals(mapOf("a" to listOf("apple")), (results.first() as Async.Success).data)
    }

    @Test
    fun `invoke should return Success with an empty group when there are no items`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(listOf())

        val results = useCase()(flowOf(""), flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

        advanceUntilIdle()

        assertEquals(mapOf(), (results.first() as Async.Success).data)
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
    fun `invoke should return Error when the group function throws`() = runTest(testDispatcher) {
        every { repository.observe(10, NO_DATE_RANGE) } returns flowOf(items)

        val useCase = ObserveGroupedUseCase<String, String, String, Map<String, List<String>>>(
            repository = repository,
            group = { throw RuntimeException("Test error") },
        )

        val results = useCase(flowOf(""), flowOf(NO_DATE_RANGE), null, null, flowOf(10)).toList()

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

    private fun useCase() = ObserveGroupedUseCase<String, String, String, Map<String, List<String>>>(
        repository = repository,
        filter = { list, type -> list.filter { it.startsWith(type) } },
        sort = { list, type -> if (type == "asc") list.sorted() else list.sortedDescending() },
        group = { list -> list.groupBy { it.first().toString() } },
    )

    companion object {

        private val items = listOf("apple", "avocado", "banana")

    }

}

