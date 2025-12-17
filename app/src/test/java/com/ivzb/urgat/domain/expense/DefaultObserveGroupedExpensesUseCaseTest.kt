package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.util.toTimestamp
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
import java.time.LocalDate
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveGroupedExpensesUseCaseTest {

    private lateinit var repository: ExpensesRepository
    private lateinit var observeUseCase: ObserveGroupedExpensesUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveGroupedExpensesUseCase(repository)
    }

    @Test
    fun `invoke should return Success with filtered items`() = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val limit = 10
        val limitFlow = flowOf(limit)
        val repositoryStream = flowOf(
        listOf(
                Expense("1", "Clothing", BigDecimal("120"), "Shopping", timestamp(2025, 11, 25), false, timestamp(2025, 11, 25)),
                Expense("2", "Groceries", BigDecimal("75.50"), "Food", timestamp(2025, 11, 27), false, timestamp(2025, 11, 27)),
                Expense("3", "Birthday gift", BigDecimal("80"), "Gifts", timestamp(2025, 12, 9), false, timestamp(2025, 12, 9)),
                Expense("4", "Pet food", BigDecimal("25.5"), "Pets", timestamp(2025, 12, 17), false, timestamp(2025, 12, 17)),
            ),
        )

        every { repository.getStream(limit, "coffee") } returns repositoryStream

        val results = observeUseCase(searchQueryFlow, null, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedItems(
                group = "December 2025",
                items = listOf(
                    Expense("4", "Pet food", BigDecimal("25.5"), "Pets", 1765922400000, false, 1765922400000),
                    Expense("3", "Birthday gift", BigDecimal("80"), "Gifts", 1765231200000, false, 1765231200000)
                ),
            ),
            GroupedItems(
                group = "November 2025",
                items = listOf(
                    Expense("2", "Groceries", BigDecimal("75.50"), "Food", 1764194400000, false, 1764194400000),
                    Expense("1", "Clothing", BigDecimal("120"), "Shopping", 1764021600000, false, 1764021600000),
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

        every { repository.getStream(limit) } throws RuntimeException("Test error")

        val results = observeUseCase(searchQueryFlow, null, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
        assertEquals(R.string.error_expenses, (first as Async.Error).errorMessage)
    }

    private fun timestamp(year: Int, month: Int, day: Int) = LocalDate.of(year, month, day).toTimestamp()
}
