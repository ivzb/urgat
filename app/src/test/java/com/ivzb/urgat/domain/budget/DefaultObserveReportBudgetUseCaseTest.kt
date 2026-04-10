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
import com.ivzb.urgat.domain.saving.GroupedSavings
import com.ivzb.urgat.data.saving.SavingTotal
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsMonthlyUseCase
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
class DefaultObserveReportBudgetUseCaseTest {

    private lateinit var observeGroupedBudgetCategoriesMonthlyUseCase: ObserveGroupedBudgetCategoriesMonthlyUseCase
    private lateinit var observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase
    private lateinit var observeGroupedInvestmentsMonthlyUseCase: ObserveGroupedInvestmentsMonthlyUseCase
    private lateinit var observeGroupedSavingsMonthlyUseCase: ObserveGroupedSavingsMonthlyUseCase
    private lateinit var forexRepository: ForexRepository

    private lateinit var observeUseCase: ObserveReportBudgetUseCase

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

        observeUseCase = DefaultObserveReportBudgetUseCase(
            observeGroupedBudgetCategoriesMonthlyUseCase,
            observeGroupedExpensesMonthlyUseCase,
            observeGroupedInvestmentsMonthlyUseCase,
            observeGroupedSavingsMonthlyUseCase,
            forexRepository,
        )
    }

    @Test
    fun `invoke should return Success with filtered items sorted`() = runTest(testDispatcher) {
        val dateRangeFlow = flowOf(NO_DATE_RANGE)

        val categories = listOf(
            GroupedBudgetCategories(
                group = "2025/11",
                total = 6800.toBigDecimal(),
                items = listOf(
                    BudgetCategory("1", "1", "EUR", 6800.toBigDecimal()),
                ),
            ),
        )

        val expenses = listOf(
            GroupedExpenses("2025/11", listOf(ExpenseTotal(1200.toBigDecimal(), "EUR")), listOf(
                Expense("1", "1", 600.toBigDecimal(), "EUR", "1", 1, false, false, 1),
                Expense("2", "2", 600.toBigDecimal(), "EUR", "2", 2, false, false, 2),
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
        )

        val savings = listOf(
            GroupedSavings("2025/11", listOf(SavingTotal(1100.toBigDecimal(), "EUR")), listOf(
                Saving("1", "Emergency fund", 500.toBigDecimal(), "EUR", 1, false, 1),
                Saving("2", "Emergency fund", 600.toBigDecimal(), "EUR", 2, false, 2),
            )),
        )

        every { observeGroupedBudgetCategoriesMonthlyUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(categories))

        every { observeGroupedExpensesMonthlyUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(expenses))

        every { observeGroupedInvestmentsMonthlyUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(investments))

        every { observeGroupedSavingsMonthlyUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(savings))

        val results = observeUseCase(dateRangeFlow).toList()

        advanceUntilIdle()

        val expected = GroupedBudget(
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
        )

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        val dateRangeFlow = flowOf(NO_DATE_RANGE)
        val limit = 10

        every { observeGroupedBudgetCategoriesMonthlyUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Error)

        every { observeGroupedExpensesMonthlyUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(listOf()))

        every { observeGroupedInvestmentsMonthlyUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(listOf()))

        every { observeGroupedSavingsMonthlyUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(listOf()))

        val results = observeUseCase(dateRangeFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }
}
