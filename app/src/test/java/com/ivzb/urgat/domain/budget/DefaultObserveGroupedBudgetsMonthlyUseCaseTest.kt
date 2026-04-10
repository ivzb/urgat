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
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsMonthlyUseCase
import com.ivzb.urgat.data.saving.SavingTotal
import com.ivzb.urgat.domain.saving.GroupedSavings
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsMonthlyUseCase
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
class DefaultObserveGroupedBudgetsMonthlyUseCaseTest {

    private lateinit var observeGroupedBudgetCategoriesMonthlyUseCase: ObserveGroupedBudgetCategoriesMonthlyUseCase
    private lateinit var observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase
    private lateinit var observeGroupedInvestmentsMonthlyUseCase: ObserveGroupedInvestmentsMonthlyUseCase
    private lateinit var observeGroupedSavingsMonthlyUseCase: ObserveGroupedSavingsMonthlyUseCase
    private lateinit var forexRepository: ForexRepository

    private lateinit var observeUseCase: ObserveGroupedBudgetsMonthlyUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        observeGroupedBudgetCategoriesMonthlyUseCase = mockk()
        observeGroupedExpensesMonthlyUseCase = mockk()
        observeGroupedInvestmentsMonthlyUseCase = mockk()
        observeGroupedSavingsMonthlyUseCase = mockk()
        forexRepository = mockk()

        coEvery { forexRepository.convert(any(), any(), any()) } answers {
            firstArg<BigDecimal>()
        }

        observeUseCase = DefaultObserveGroupedBudgetsMonthlyUseCase(
            observeGroupedBudgetCategoriesMonthlyUseCase,
            observeGroupedExpensesMonthlyUseCase,
            observeGroupedInvestmentsMonthlyUseCase,
            observeGroupedSavingsMonthlyUseCase,
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

        val categories = listOf(
            GroupedBudgetCategories(
                group = "2025/11",
                total = 6800.toBigDecimal(),
                items = listOf(
                    BudgetCategory("1", "1", "EUR", 6800.toBigDecimal()),
                ),
            ),
            GroupedBudgetCategories(
                group = "2025/12",
                total = 7000.toBigDecimal(),
                items = listOf(
                    BudgetCategory("2", "2", "EUR", 2600.toBigDecimal()),
                    BudgetCategory("3", "3", "EUR", 1000.toBigDecimal()),
                    BudgetCategory("4", "4", "EUR", 3400.toBigDecimal()),
                ),
            ),
            GroupedBudgetCategories(
                group = "2026/01",
                total = 6600.toBigDecimal(),
                items = listOf(
                    BudgetCategory("5", "5", "EUR", 6600.toBigDecimal()),
                ),
            ),
        )

        val expenses = listOf(
            GroupedExpenses("2025/11", listOf(ExpenseTotal(1200.toBigDecimal(), "EUR")), listOf(
                Expense("1", "1", 600.toBigDecimal(), "EUR", "1", 1, false, false, 1),
                Expense("2", "2", 600.toBigDecimal(), "EUR", "2", 2, false, false, 2),
            )),
            GroupedExpenses("2025/12", listOf(ExpenseTotal(1400.toBigDecimal(), "EUR")), listOf(
                Expense("3", "3", 1400.toBigDecimal(), "EUR", "3", 3, false, false, 3),
            )),
        )

        val investments = listOf(
            GroupedInvestments(
                group = "2025/11",
                total = listOf(
                    InvestmentTotal("EUR", 1800.toBigDecimal(), 1800.toBigDecimal(), 0.toBigDecimal()),
                ),
                items = listOf(
                    Investment("1", "1", 1.toBigDecimal(), 900.toBigDecimal(), 900.toBigDecimal(), "1", "EUR", 1, false, 1),
                    Investment("2", "2", 1.toBigDecimal(), 900.toBigDecimal(), 900.toBigDecimal(), "2", "EUR", 2, false, 2),
                ),
            ),
            GroupedInvestments(
                group = "2025/12",
                total = listOf(
                    InvestmentTotal("USD", 2000.toBigDecimal(), 2000.toBigDecimal(), 0.toBigDecimal()),
                ),
                items = listOf(
                    Investment("3", "3", 1.toBigDecimal(), 2000.toBigDecimal(), 2000.toBigDecimal(), "3", "USD", 3, false, 3),
                ),
            ),
        )

        val savings = listOf(
            GroupedSavings("2025/11", listOf(SavingTotal(1100.toBigDecimal(), "EUR")), listOf(
                Saving("1", "Emergency fund", 500.toBigDecimal(), "EUR", 1, false, 1),
                Saving("2", "Emergency fund", 600.toBigDecimal(), "EUR", 2, false, 2),
            )),
            GroupedSavings("2025/12", listOf(SavingTotal(700.toBigDecimal(), "EUR")), listOf(
                Saving("3", "Emergency fund", 700.toBigDecimal(), "EUR", 3, false, 3),
            )),
        )

        every { observeGroupedBudgetCategoriesMonthlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = eq(sortTypeFlow),
            limitFlow = null,
        ) } returns flowOf(Async.Success(categories))

        every { observeGroupedExpensesMonthlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(expenses))

        every { observeGroupedInvestmentsMonthlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(investments))

        every { observeGroupedSavingsMonthlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(savings))

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        val expected = listOf(
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025/11",
                    total = 6800.toBigDecimal(),
                    items = listOf(
                        BudgetCategory("1", "1", "EUR", 6800.toBigDecimal()),
                    ),
                ),
                currency = "EUR",
                totalIncome = 6800.toBigDecimal(),
                totalExpenses = 1200.toBigDecimal(),
                totalInvestments = 1_800.toBigDecimal(),
                totalSavings = 1100.toBigDecimal(),
            ),
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2025/12",
                    total = 7000.toBigDecimal(),
                    items = listOf(
                        BudgetCategory("2", "2", "EUR", 2600.toBigDecimal()),
                        BudgetCategory("3", "3", "EUR", 1000.toBigDecimal()),
                        BudgetCategory("4", "4", "EUR", 3400.toBigDecimal()),
                    ),
                ),
                currency = "EUR",
                totalIncome = 7000.toBigDecimal(),
                totalExpenses = 1400.toBigDecimal(),
                totalInvestments = 2_000.toBigDecimal(),
                totalSavings = 700.toBigDecimal(),
            ),
            GroupedBudget(
                category = GroupedBudgetCategories(
                    group = "2026/01",
                    total = 6600.toBigDecimal(),
                    items = listOf(
                        BudgetCategory("5", "5", "EUR", 6600.toBigDecimal()),
                    ),
                ),
                currency = "EUR",
                totalIncome = 6600.toBigDecimal(),
                totalExpenses = 0.toBigDecimal(),
                totalInvestments = 0.toBigDecimal(),
                totalSavings = 0.toBigDecimal(),
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
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10
        val sortTypeFlow = flowOf(BudgetsSortType.BY_DATE)
        val limitFlow = flowOf(limit)

        every { observeGroupedBudgetCategoriesMonthlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = eq(sortTypeFlow),
            limitFlow = null,
        ) } returns flowOf(Async.Error)

        every { observeGroupedExpensesMonthlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(listOf()))

        every { observeGroupedInvestmentsMonthlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(listOf()))

        every { observeGroupedSavingsMonthlyUseCase.invoke(
            searchQueryFlow = eq(searchQueryFlow),
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(listOf()))

        val results = observeUseCase(searchQueryFlow, dateRangeFlow, null, sortTypeFlow, limitFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }
}
