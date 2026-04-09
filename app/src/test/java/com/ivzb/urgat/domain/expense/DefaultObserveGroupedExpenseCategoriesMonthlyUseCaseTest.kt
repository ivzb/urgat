package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.expense.ExpenseTotal
import com.ivzb.urgat.data.expense.ExpensesRepository
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
class ObserveGroupedExpenseCategoriesMonthlyUseCaseTest {

    private lateinit var expensesRepository: ExpensesRepository
    private lateinit var randomRepository: RandomRepository
    private lateinit var observeUseCase: ObserveGroupedExpenseCategoriesMonthlyUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        expensesRepository = mockk()
        randomRepository = mockk()
        observeUseCase = DefaultObserveGroupedExpenseCategoriesMonthlyUseCase(expensesRepository, randomRepository)

        every { randomRepository.uuid() } returns TEST_UUID
    }

    @Test
    fun `invoke should return Success with filtered items`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)
        val expenses = listOf(
            Expense("1", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("2", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("3", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("4", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
        )
        val repositoryStream = flowOf(expenses)

        every { expensesRepository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "December 2025",
                total = listOf(ExpenseTotal(BigDecimal("105.5"), "EUR")),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Gifts", totals = listOf(ExpenseTotal(BigDecimal("80"), "EUR")), expenses = listOf(expenses[2])),
                    ExpenseCategory(id = TEST_UUID, "Pets", totals = listOf(ExpenseTotal(BigDecimal("25.5"), "EUR")), expenses = listOf(expenses[3])),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = listOf(ExpenseTotal(BigDecimal("195.50"), "EUR")),
                items = listOf(
                    ExpenseCategory(id = TEST_UUID, "Shopping", totals = listOf(ExpenseTotal(BigDecimal("120"), "EUR")), expenses = listOf(expenses[0])),
                    ExpenseCategory(id = TEST_UUID, "Food", totals = listOf(ExpenseTotal(BigDecimal("75.50"), "EUR")), expenses = listOf(expenses[1])),
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
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)

        every { expensesRepository.observe(limit, NO_DATE_RANGE) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }

    companion object {

        private const val TEST_UUID = "mock_id"

    }

}
