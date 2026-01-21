package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.BudgetsRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.timestamp
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
class ObserveGroupedBudgetCategoriesYearlyUseCaseTest {

    private lateinit var budgetsRepository: BudgetsRepository
    private lateinit var randomRepository: RandomRepository
    private lateinit var observeUseCase: ObserveGroupedBudgetCategoriesYearlyUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        budgetsRepository = mockk()
        randomRepository = mockk()
        observeUseCase = DefaultObserveGroupedBudgetCategoriesYearlyUseCase(
            budgetsRepository,
            randomRepository
        )

        every { randomRepository.uuid() } returns TEST_UUID
    }

    @Test
    fun `invoke should return Success with filtered items`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val limit = 10
        val limitFlow = flowOf(limit)
        val budgets = listOf(
            Budget("1", 3800.toBigDecimal(), 5600.toBigDecimal(), 6800.toBigDecimal(), timestamp(2026, 1, 25), false, timestamp(2026, 1, 25)),
            Budget("2", 3400.toBigDecimal(), 5400.toBigDecimal(), 6600.toBigDecimal(), timestamp(2026, 1, 10), false, timestamp(2026, 1, 10)),
            Budget("3", 3200.toBigDecimal(), 5200.toBigDecimal(), 6400.toBigDecimal(), timestamp(2025, 12, 25), false, timestamp(2025, 12, 25)),
            Budget("4", 3000.toBigDecimal(), 5000.toBigDecimal(), 6200.toBigDecimal(), timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
            Budget("5", 2800.toBigDecimal(), 4800.toBigDecimal(), 6000.toBigDecimal(), timestamp(2025, 10, 25), false, timestamp(2025, 10, 25)),
            Budget("6", 2600.toBigDecimal(), 4600.toBigDecimal(), 5800.toBigDecimal(), timestamp(2025, 9, 25), false, timestamp(2025, 9, 25)),
        )
        val repositoryStream = flowOf(budgets)

        every { budgetsRepository.observe(limit) } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "2026",
                total = "7200".toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = TEST_UUID, "1", BigDecimal("3800")),
                    BudgetCategory(id = TEST_UUID, "2", BigDecimal("3400")),
                ),
            ),
            GroupedItems(
                group = "2025",
                total = "11600".toBigDecimal(),
                items = listOf(
                    BudgetCategory(id = TEST_UUID, "3", BigDecimal("3200")),
                    BudgetCategory(id = TEST_UUID, "4", BigDecimal("3000")),
                    BudgetCategory(id = TEST_UUID, "5", BigDecimal("2800")),
                    BudgetCategory(id = TEST_UUID, "6", BigDecimal("2600")),
                ),
            ),
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("")
        val limit = 10
        val limitFlow = flowOf(limit)

        every { budgetsRepository.observe(limit) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }

    companion object {

        private const val TEST_UUID = "mock_id"

    }

}
