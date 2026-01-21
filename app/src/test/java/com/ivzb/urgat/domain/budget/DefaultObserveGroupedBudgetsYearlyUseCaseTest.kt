package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.domain.expense.GroupedExpenses
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesYearlyUseCase
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsYearlyUseCase
import com.ivzb.urgat.ui.budgets.BudgetsSortType
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
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultObserveGroupedBudgetsYearlyUseCaseTest {

    private lateinit var observeGroupedBudgetCategoriesYearlyUseCase: ObserveGroupedBudgetCategoriesYearlyUseCase
    private lateinit var observeGroupedExpensesYearlyUseCase: ObserveGroupedExpensesYearlyUseCase
    private lateinit var observeGroupedInvestmentsYearlyUseCase: ObserveGroupedInvestmentsYearlyUseCase

    private lateinit var observeUseCase: ObserveGroupedBudgetsYearlyUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        observeGroupedBudgetCategoriesYearlyUseCase = mockk()
        observeGroupedExpensesYearlyUseCase = mockk()
        observeGroupedInvestmentsYearlyUseCase = mockk()

        observeUseCase = DefaultObserveGroupedBudgetsYearlyUseCase(
            observeGroupedBudgetCategoriesYearlyUseCase,
            observeGroupedExpensesYearlyUseCase,
            observeGroupedInvestmentsYearlyUseCase,
        )
    }

    @Test
    fun `invoke should return Success with filtered items sorted by date`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted`(BudgetsSortType.BY_DATE)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by min`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted`(BudgetsSortType.BY_MIN)
    }

    @Test
    fun `invoke should return Success with filtered items sorted by max`() = runTest(testDispatcher) {
        `invoke should return Success with filtered items sorted`(BudgetsSortType.BY_MAX)
    }

    private fun `invoke should return Success with filtered items sorted`(sortType: BudgetsSortType) = runTest(testDispatcher) {
        val searchQueryFlow = flowOf("coffee")
        val limit = 10
        val sortTypeFlow = flowOf(sortType)
        val limitFlow = flowOf(limit)

        val categories = listOf(
            GroupedBudgetCategories(
                group = "2025",
                total = 13800.toBigDecimal(),
                items = listOf(
                    BudgetCategory("1", "1", 6800.toBigDecimal()),
                    BudgetCategory("2", "2", 2600.toBigDecimal()),
                    BudgetCategory("3", "3", 1000.toBigDecimal()),
                    BudgetCategory("4", "4", 3400.toBigDecimal()),
                ),
            ),
            GroupedBudgetCategories(
                group = "2026",
                total = 6600.toBigDecimal(),
                items = listOf(
                    BudgetCategory("5", "5", 6600.toBigDecimal()),
                ),
            ),
        )

        val expenses = listOf(
            GroupedExpenses("2025", 2600.toBigDecimal(), listOf(
                Expense("1", "1", 600.toBigDecimal(), "1", 1, false, false, 1),
                Expense("2", "2", 600.toBigDecimal(), "2", 2, false, false, 2),
                Expense("3", "3", 1400.toBigDecimal(), "3", 3, false, false, 3),
            )),
        )

        val investments = listOf(
            GroupedInvestments("2025", "1800 EUR | 2000 USD", listOf(
                Investment("1", "1", 1.toBigDecimal(), 900.toBigDecimal(), "1", "EUR", 1, false, 1),
                Investment("2", "2", 1.toBigDecimal(), 900.toBigDecimal(), "2", "EUR", 2, false, 2),
                Investment("3", "3", 1.toBigDecimal(), 2000.toBigDecimal(), "3", "USD", 3, false, 3),
            )),
        )

        every { observeGroupedBudgetCategoriesYearlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            filterTypeFlow = null,
            sortTypeFlow = eq(sortTypeFlow),
            limitFlow = null,
        ) } returns flowOf(Async.Success(categories))

        every { observeGroupedExpensesYearlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(expenses))

        every { observeGroupedInvestmentsYearlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(investments))

        val results = observeUseCase(searchQueryFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025",
                    total = 13800.toBigDecimal(),
                    items = listOf(
                        BudgetCategory("1", "1", 6800.toBigDecimal()),
                        BudgetCategory("2", "2", 2600.toBigDecimal()),
                        BudgetCategory("3", "3", 1000.toBigDecimal()),
                        BudgetCategory("4", "4", 3400.toBigDecimal()),
                    ),
                ),
                totalIncome = 13800.toBigDecimal(),
                totalExpenses = 2600.toBigDecimal(),
                totalInvestments = 3800.toBigDecimal(),
                totalInvestmentsFormatted = "1800 EUR | 2000 USD",
            ),
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2026",
                    total = 6600.toBigDecimal(),
                    items = listOf(
                        BudgetCategory("5", "5", 6600.toBigDecimal()),
                    ),
                ),
                totalIncome = 6600.toBigDecimal(),
                totalExpenses = 0.toBigDecimal(),
                totalInvestments = 0.toBigDecimal(),
                totalInvestmentsFormatted = "",
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
        val searchQueryFlow = flowOf("coffee")
        val limit = 10
        val sortTypeFlow = flowOf(BudgetsSortType.BY_DATE)
        val limitFlow = flowOf(limit)

        every { observeGroupedBudgetCategoriesYearlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            filterTypeFlow = null,
            sortTypeFlow = eq(sortTypeFlow),
            limitFlow = null,
        ) } returns flowOf(Async.Error)

        every { observeGroupedExpensesYearlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(listOf()))

        every { observeGroupedInvestmentsYearlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(listOf()))

        val results = observeUseCase(searchQueryFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }
}
