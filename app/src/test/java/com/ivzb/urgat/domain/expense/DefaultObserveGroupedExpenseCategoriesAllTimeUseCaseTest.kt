package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.util.Async
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
class ObserveGroupedExpenseCategoriesAllTimeUseCaseTest {

    private lateinit var expensesRepository: ExpensesRepository
    private lateinit var randomRepository: RandomRepository
    private lateinit var observeUseCase: ObserveGroupedExpenseCategoriesAllTimeUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        expensesRepository = mockk()
        randomRepository = mockk()
        observeUseCase = DefaultObserveGroupedExpenseCategoriesAllTimeUseCase(expensesRepository, randomRepository)

        every { randomRepository.uuid() } returns TEST_UUID
    }

    @Test
    fun `invoke should return Success with filtered items`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val limitFlow = flowOf(limit)
        val expenses = listOf(
            Expense("1", "Clothing", BigDecimal("120"), "Shopping", timestamp(2025, 11, 25), false, false, timestamp(2025, 11, 25)),
            Expense("2", "Groceries", BigDecimal("75.50"), "Food", timestamp(2025, 11, 27), false, false, timestamp(2025, 11, 27)),
            Expense("3", "Birthday gift", BigDecimal("80"), "Gifts", timestamp(2025, 12, 9), false, false, timestamp(2025, 12, 9)),
            Expense("4", "Pet food", BigDecimal("25.5"), "Pets", timestamp(2025, 12, 17), false, false, timestamp(2025, 12, 17)),
            Expense("5", "Gas", BigDecimal("304.27"), "Fuel", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
            Expense("6", "Groceries", BigDecimal("150"), "Food", timestamp(2024, 12, 17), false, false, timestamp(2024, 12, 17)),
        )
        val repositoryStream = flowOf(expenses)

        every { expensesRepository.observe(limit, NO_DATE_RANGE, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, null, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            ExpenseCategory(id = TEST_UUID, "Fuel", BigDecimal("304.27"), expenses = listOf(expenses[4])),
            ExpenseCategory(id = TEST_UUID, "Food", BigDecimal("225.50"), expenses = listOf(expenses[1], expenses[5])),
            ExpenseCategory(id = TEST_UUID, "Shopping", BigDecimal("120"), expenses = listOf(expenses[0])),
            ExpenseCategory(id = TEST_UUID, "Gifts", BigDecimal("80"), expenses = listOf(expenses[2])),
            ExpenseCategory(id = TEST_UUID, "Pets", BigDecimal("25.5"), expenses = listOf(expenses[3])),
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
