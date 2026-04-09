package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseTotal
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.expenses.ExpensesSortType
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
class DefaultObserveGroupedExpensesMonthlyUseCaseTest {

    private lateinit var repository: ExpensesRepository
    private lateinit var observeUseCase: ObserveGroupedExpensesMonthlyUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveGroupedExpensesMonthlyUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by date`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(ExpensesSortType.BY_DATE)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
        listOf(
                Expense("1", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
                Expense("2", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
                Expense("3", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
                Expense("4", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "December 2025",
                total = listOf(ExpenseTotal(BigDecimal("105.5"), "EUR")),
                items = listOf(
                    Expense("4", "Pet food", BigDecimal("25.5"), "EUR", "Pets", 1765922400000, false, false, 1765922400000),
                    Expense("3", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", 1765231200000, false, false, 1765231200000)
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = listOf(ExpenseTotal(BigDecimal("195.50"), "EUR")),
                items = listOf(
                    Expense("2", "Groceries", BigDecimal("75.50"), "EUR", "Food", 1764194400000, false, false, 1764194400000),
                    Expense("1", "Clothing", BigDecimal("120"), "EUR", "Shopping", 1764021600000, false, false, 1764021600000),
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
    fun `invoke should return Success with filtered items sorted by min`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(ExpensesSortType.BY_MIN)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Expense("1", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
                Expense("2", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
                Expense("3", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
                Expense("4", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "December 2025",
                total = listOf(ExpenseTotal(BigDecimal("105.5"), "EUR")),
                items = listOf(
                    Expense("4", "Pet food", BigDecimal("25.5"), "EUR", "Pets", 1765922400000, false, false, 1765922400000),
                    Expense("3", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", 1765231200000, false, false, 1765231200000),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = listOf(ExpenseTotal(BigDecimal("195.50"), "EUR")),
                items = listOf(
                    Expense("2", "Groceries", BigDecimal("75.50"), "EUR", "Food", 1764194400000, false, false, 1764194400000),
                    Expense("1", "Clothing", BigDecimal("120"), "EUR", "Shopping", 1764021600000, false, false, 1764021600000),
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
    fun `invoke should return Success with filtered items sorted by max`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(ExpensesSortType.BY_MAX)
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
            listOf(
                Expense("1", "Clothing", BigDecimal("120"), "EUR", "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
                Expense("2", "Groceries", BigDecimal("75.50"), "EUR", "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
                Expense("3", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
                Expense("4", "Pet food", BigDecimal("25.5"), "EUR", "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            ),
        )

        every { repository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "December 2025",
                total = listOf(ExpenseTotal(BigDecimal("105.5"), "EUR")),
                items = listOf(
                    Expense("3", "Birthday gift", BigDecimal("80"), "EUR", "Gifts", 1765231200000, false, false, 1765231200000),
                    Expense("4", "Pet food", BigDecimal("25.5"), "EUR", "Pets", 1765922400000, false, false, 1765922400000),
                ),
            ),
            GroupedItems(
                group = "November 2025",
                total = listOf(ExpenseTotal(BigDecimal("195.50"), "EUR")),
                items = listOf(
                    Expense("1", "Clothing", BigDecimal("120"), "EUR", "Shopping", 1764021600000, false, false, 1764021600000),
                    Expense("2", "Groceries", BigDecimal("75.50"), "EUR", "Food", 1764194400000, false, false, 1764194400000),
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

        every { repository.observe(limit, NO_DATE_RANGE) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }
}
