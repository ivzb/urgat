package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetsRepository
import com.ivzb.urgat.util.Async
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
import java.math.BigDecimal
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveBudgetsUseCaseTest {

    private lateinit var repository: BudgetsRepository
    private lateinit var observeUseCase: ObserveBudgetsUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveBudgetsUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("budget")
        val limit = 10
        val limitFlow = flowOf(limit)

        val repositoryStream = flowOf(
            listOf(
                Budget("1", BigDecimal("6500"), BigDecimal("6500"), BigDecimal("-1250"), 1L, false, 1L),
                Budget("2", BigDecimal("6500"), BigDecimal("6500"), BigDecimal("1250"), 1L, false, 1L),
            )
        )

        every { repository.observe(limit, "budget") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val items = (first as Async.Success).data
        assertEquals(2, items.size)
        assertEquals("6500".toBigDecimal(), items[0].income)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val limit = 10
        val limitFlow = flowOf(limit)

        every { repository.observe(limit) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }
}
