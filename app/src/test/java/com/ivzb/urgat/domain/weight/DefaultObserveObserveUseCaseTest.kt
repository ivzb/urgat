package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.data.weight.WeightsRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.R
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveWeightsUseCaseTest {

    private lateinit var repository: WeightsRepository
    private lateinit var observeUseCase: ObserveWeightsUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveWeightsUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("weight")
        val limit = 10
        val repositoryStream = flowOf(
            listOf(
                Weight("1", 79.5.toBigDecimal(), false, createdAt = 100_000_000),
                Weight("2", 81.3.toBigDecimal(), false, createdAt = 200_000_000)
            )
        )

        every { repository.getStream(limit, "weight") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, null, limit).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val items = (first as Async.Success).data
        assertEquals(2, items.size)
        assertEquals(79.5.toBigDecimal(), items[0].value)
        assertEquals(81.3.toBigDecimal(), items[1].value)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val limit = 10

        every { repository.getStream(limit) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, null, limit).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
        assertEquals(R.string.error_weights, (first as Async.Error).errorMessage)
    }
}
