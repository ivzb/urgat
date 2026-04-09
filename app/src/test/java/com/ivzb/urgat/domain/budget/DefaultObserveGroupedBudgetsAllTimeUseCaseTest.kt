package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseTotal
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.domain.expense.GroupedExpenses
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesAllTimeUseCase
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsAllTimeUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsAllTimeUseCase
import com.ivzb.urgat.domain.saving.GroupedSavings
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.util.Async
import io.mockk.coEvery
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
class DefaultObserveGroupedBudgetsAllTimeUseCaseTest {

    private lateinit var observeGroupedBudgetCategoriesAllTimeUseCase: ObserveGroupedBudgetCategoriesAllTimeUseCase
    private lateinit var observeGroupedExpensesAllTimeUseCase: ObserveGroupedExpensesAllTimeUseCase
    private lateinit var observeGroupedInvestmentsAllTimeUseCase: ObserveGroupedInvestmentsAllTimeUseCase
    private lateinit var observeGroupedSavingsAllTimeUseCase: ObserveGroupedSavingsAllTimeUseCase
    private lateinit var forexRepository: ForexRepository

    private lateinit var observeUseCase: ObserveGroupedBudgetsAllTimeUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        observeGroupedBudgetCategoriesAllTimeUseCase = mockk()
        observeGroupedExpensesAllTimeUseCase = mockk()
        observeGroupedInvestmentsAllTimeUseCase = mockk()
        observeGroupedSavingsAllTimeUseCase = mockk()
        forexRepository = mockk()

        coEvery { forexRepository.convert(any(), any(), any()) } answers {
            firstArg<BigDecimal>()
        }

        observeUseCase = DefaultObserveGroupedBudgetsAllTimeUseCase(
            observeGroupedBudgetCategoriesAllTimeUseCase,
            observeGroupedExpensesAllTimeUseCase,
            observeGroupedInvestmentsAllTimeUseCase,
            observeGroupedSavingsAllTimeUseCase,
            forexRepository,
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
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(sortType)
        val limitFlow = flowOf(limit)

        val budget = BudgetCategory(
            id = "",
            name = "",
            currency = "EUR",
            value = 20400.toBigDecimal(),
        )

        val expenses = GroupedExpenses("2025", listOf(ExpenseTotal(2600.toBigDecimal(), "EUR")), listOf(
            Expense("1", "1", 600.toBigDecimal(), "EUR", "1", 1, false, false, 1),
            Expense("2", "2", 600.toBigDecimal(), "EUR", "2", 2, false, false, 2),
            Expense("3", "3", 1400.toBigDecimal(), "EUR", "3", 3, false, false, 3),
        ))

        val investments = GroupedInvestments(
            "2025",
            listOf(
                InvestmentTotal("EUR", 1800.toBigDecimal(), 1800.toBigDecimal(), 0.toBigDecimal()),
                InvestmentTotal("USD", 2000.toBigDecimal(), 2000.toBigDecimal(), 0.toBigDecimal()),
            ),
            listOf(
                Investment("1", "1", 1.toBigDecimal(), 900.toBigDecimal(), 900.toBigDecimal(), "1", "EUR", 1, false, 1),
                Investment("2", "2", 1.toBigDecimal(), 900.toBigDecimal(), 900.toBigDecimal(), "2", "EUR", 2, false, 2),
                Investment("3", "3", 1.toBigDecimal(), 2000.toBigDecimal(), 2000.toBigDecimal(), "3", "USD", 3, false, 3),
            )
        )

        val savings = GroupedSavings("2025", 1500.toBigDecimal(), listOf(
            Saving("1", "Emergency fund", 500.toBigDecimal(), "EUR", 1, false, 1),
            Saving("2", "Emergency fund", 600.toBigDecimal(), "EUR", 2, false, 2),
            Saving("3", "Emergency fund", 700.toBigDecimal(), "EUR", 3, false, 3),
        ))

        every { observeGroupedBudgetCategoriesAllTimeUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = eq(sortTypeFlow),
            limitFlow = null,
        ) } returns flowOf(Async.Success(budget))

        every { observeGroupedExpensesAllTimeUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(expenses))

        every { observeGroupedInvestmentsAllTimeUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(investments))

        every { observeGroupedSavingsAllTimeUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(savings))

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = GroupedBudget(
            category = GroupedBudgetCategories(
                group = "",
                total = 20400.toBigDecimal(),
                items = listOf(
                    BudgetCategory("", "", "EUR", 20400.toBigDecimal()),
                ),
            ),
            currency = "EUR",
            totalIncome = 20400.toBigDecimal(),
            totalExpenses = 2600.toBigDecimal(),
            totalInvestments = 3_800.toBigDecimal(),
            totalSavings = 1500.toBigDecimal(),
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
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(BudgetsSortType.BY_DATE)
        val limitFlow = flowOf(limit)

        every { observeGroupedBudgetCategoriesAllTimeUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = eq(sortTypeFlow),
            limitFlow = null,
        ) } returns flowOf(Async.Error)

        every { observeGroupedExpensesAllTimeUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(GroupedExpenses(
            group = "",
            total = listOf(),
            items = listOf(),
        )))

        every { observeGroupedInvestmentsAllTimeUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(GroupedInvestments(
            group = "",
            total = emptyList(),
            items = listOf(),
        )))

        every { observeGroupedSavingsAllTimeUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(GroupedSavings(
            group = "",
            total = 0.toBigDecimal(),
            items = listOf(),
        )))

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }
}
