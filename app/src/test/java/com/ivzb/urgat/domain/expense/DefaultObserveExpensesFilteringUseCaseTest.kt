package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.util.Async
import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import io.mockk.every
import io.mockk.mockk
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

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveExpensesFilteringUseCaseTest {

    private lateinit var repository: ExpensesRepository
    private lateinit var observeUseCase: ObserveExpensesFilteringUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveExpensesFilteringUseCase(repository)
    }

    @Test
    fun `invoke should return Success with grouped items`() = runTest(testDispatcher) {
        val expected = listOf(
            FilterMenuItem(type = ExpensesFilterType.ALL_EXPENSES, labelResId = R.string.filter_all, size = 5),
            FilterMenuItem(type = ExpensesFilterType.DEBIT_EXPENSES, labelResId = R.string.filter_debit, size = 2),
            FilterMenuItem(type = ExpensesFilterType.COST_EXPENSES, labelResId = R.string.filter_cost, size = 3),
        )

        val repositoryStream = flowOf(
            listOf(
                Expense("1", "food", 50.toBigDecimal()),
                Expense("1", "transport", 65.toBigDecimal()),
                Expense("1", "housing", 800.toBigDecimal()),
                Expense("1", "income", -500.toBigDecimal()),
                Expense("1", "income", -40.toBigDecimal()),
            )
        )

        every { repository.getStream(NO_LIMIT) } returns repositoryStream

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val items = (first as Async.Success).data
        assertEquals(expected, items)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        every { repository.getStream(NO_LIMIT) } returns flow {
            throw RuntimeException("Test error")
        }

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
        assertEquals(R.string.error_expenses_filtering, (first as Async.Error).errorMessage)
    }
}
