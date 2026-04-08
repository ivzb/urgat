package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.ui.expenses.ExpensesSortType
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
class ObserveExpensesUseCaseTest {

    private lateinit var repository: ExpensesRepository
    private lateinit var observeUseCase: ObserveExpensesUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveExpensesUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by date`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted by`(
            sortType = ExpensesSortType.BY_DATE,
            expected = listOf(
                Expense("3", "Juice", BigDecimal("4"), "EUR", "Food", 125, false, false, 1L),
                Expense("2", "Tea", BigDecimal("2.0"), "EUR", "Food", 124, false, false, 1L),
                Expense("1", "Coffee", BigDecimal("3.5"), "EUR", "Food", 123, false, false, 1L),
            ),
        )
    }

    @Test
    fun `invoke should return Success with filtered items sorted by min`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted by`(
            sortType = ExpensesSortType.BY_MIN,
            expected = listOf(
                Expense("2", "Tea", BigDecimal("2.0"), "EUR", "Food", 124, false, false, 1L),
                Expense("1", "Coffee", BigDecimal("3.5"), "EUR", "Food", 123, false, false, 1L),
                Expense("3", "Juice", BigDecimal("4"), "EUR", "Food", 125, false, false, 1L),
            ),
        )
    }

    @Test
    fun `invoke should return Success with filtered items sorted by max`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted by`(
            sortType = ExpensesSortType.BY_MAX,
            expected = listOf(
                Expense("3", "Juice", BigDecimal("4"), "EUR", "Food", 125, false, false, 1L),
                Expense("1", "Coffee", BigDecimal("3.5"), "EUR", "Food", 123, false, false, 1L),
                Expense("2", "Tea", BigDecimal("2.0"), "EUR", "Food", 124, false, false, 1L),
            ),
        )
    }

    private fun `invoke should return Success with filtered items sorted by`(sortType: ExpensesSortType, expected: List<Expense>) = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val sortTypeFlow = flowOf(sortType)
        val limit = 10
        val limitFlow = flowOf(limit)

        val repositoryStream = flowOf(
            listOf(
                Expense("1", "Coffee", BigDecimal("3.5"), "EUR", "Food", 123, false, false, 1L),
                Expense("2", "Tea", BigDecimal("2.0"), "EUR", "Food", 124, false, false, 1L),
                Expense("3", "Juice", BigDecimal("4"), "EUR", "Food", 125, false, false, 1L),
            )
        )

        every { repository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

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
