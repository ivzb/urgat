package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.expense.GroupedExpenses
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsAllTimeUseCase
import com.ivzb.urgat.domain.investment.GroupedInvestments
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsAllTimeUseCase
import com.ivzb.urgat.domain.saving.GroupedSavings
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
class DefaultObserveDashboardBudgetUseCaseTest {

    private lateinit var observeGroupedBudgetCategoriesMonthlyUseCase: ObserveGroupedBudgetCategoriesMonthlyUseCase
    private lateinit var observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase
    private lateinit var observeGroupedInvestmentsAllTimeUseCase: ObserveGroupedInvestmentsAllTimeUseCase
    private lateinit var observeGroupedSavingsAllTimeUseCase: ObserveGroupedSavingsAllTimeUseCase

    private lateinit var observeUseCase: ObserveFiReUseCase

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        observeGroupedBudgetCategoriesMonthlyUseCase = mockk()
        observeGroupedExpensesMonthlyUseCase = mockk()
        observeGroupedInvestmentsAllTimeUseCase = mockk()
        observeGroupedSavingsAllTimeUseCase = mockk()

        observeUseCase = DefaultObserveFiReUseCase(
            observeGroupedBudgetCategoriesMonthlyUseCase,
            observeGroupedExpensesMonthlyUseCase,
            observeGroupedInvestmentsAllTimeUseCase,
            observeGroupedSavingsAllTimeUseCase,
        )
    }

    @Test
    fun `invoke should return Success 40 percent investments with stash`() = `invoke should return Success with filtered items`(
        expenses = listOf(
            GroupedExpenses("December 2025", 3000.toBigDecimal(), listOf(
                Expense("1", "1", 1500.toBigDecimal(), "1", 1, false, false, 1),
                Expense("2", "2", 1500.toBigDecimal(), "2", 2, false, false, 2),
            )),
        ),
        expected = EXPECTED_FIRE.copy(
            monthlyExpenses = "3000".toBigDecimal(),
            monthlyInvestments = "2000".toBigDecimal(),
            annualExpenses = "36000".toBigDecimal(),
            annualInvestments = "24000".toBigDecimal(),
            savingsRate = "40.00".toBigDecimal(),
            target = "990000.00".toBigDecimal(),
            yearsUntilFiRe = "17.74".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success 40 percent investments without stash`() = `invoke should return Success with filtered items`(
        expenses = listOf(
            GroupedExpenses("December 2025", 3000.toBigDecimal(), listOf(
                Expense("1", "1", 1500.toBigDecimal(), "1", 1, false, false, 1),
                Expense("2", "2", 1500.toBigDecimal(), "2", 2, false, false, 2),
            )),
        ),
        investments = NO_INVESTMENTS,
        savings = NO_SAVINGS,
        expected = EXPECTED_FIRE.copy(
            monthlyExpenses = "3000".toBigDecimal(),
            monthlyInvestments = "2000".toBigDecimal(),
            annualExpenses = "36000".toBigDecimal(),
            annualInvestments = "24000".toBigDecimal(),
            accumulatedStash = "0.00".toBigDecimal(),
            savingsRate = "40.00".toBigDecimal(),
            target = "990000.00".toBigDecimal(),
            yearsUntilFiRe = "19.69".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success 50 percent investments with stash`() = `invoke should return Success with filtered items`()

    @Test
    fun `invoke should return Success 50 percent investments without stash`() = `invoke should return Success with filtered items`(
        investments = NO_INVESTMENTS,
        savings = NO_SAVINGS,
        expected = EXPECTED_FIRE.copy(
            accumulatedStash = "0.00".toBigDecimal(),
            target = "825000.00".toBigDecimal(),
            yearsUntilFiRe = "15.53".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success 60 percent investments with stash`() = `invoke should return Success with filtered items`(
        expenses = listOf(
            GroupedExpenses("December 2025", 2000.toBigDecimal(), listOf(
                Expense("1", "1", 500.toBigDecimal(), "1", 1, false, false, 1),
                Expense("2", "2", 1500.toBigDecimal(), "2", 2, false, false, 2),
            )),
        ),
        expected = EXPECTED_FIRE.copy(
            monthlyExpenses = "2000".toBigDecimal(),
            monthlyInvestments = "3000".toBigDecimal(),
            annualExpenses = "24000".toBigDecimal(),
            annualInvestments = "36000".toBigDecimal(),
            savingsRate = "60.00".toBigDecimal(),
            target = "660000.00".toBigDecimal(),
            yearsUntilFiRe = "10.59".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success 60 percent investments without stash`() = `invoke should return Success with filtered items`(
        expenses = listOf(
            GroupedExpenses("December 2025", 2000.toBigDecimal(), listOf(
                Expense("1", "1", 500.toBigDecimal(), "1", 1, false, false, 1),
                Expense("2", "2", 1500.toBigDecimal(), "2", 2, false, false, 2),
            )),
        ),
        investments = NO_INVESTMENTS,
        savings = NO_SAVINGS,
        expected = EXPECTED_FIRE.copy(
            monthlyExpenses = "2000".toBigDecimal(),
            monthlyInvestments = "3000".toBigDecimal(),
            annualExpenses = "24000".toBigDecimal(),
            annualInvestments = "36000".toBigDecimal(),
            accumulatedStash = "0.00".toBigDecimal(),
            savingsRate = "60.00".toBigDecimal(),
            target = "660000.00".toBigDecimal(),
            yearsUntilFiRe = "11.92".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success 100 percent investments with stash`() = `invoke should return Success with filtered items`(
        expenses = listOf(
            GroupedExpenses("December 2025", 0.toBigDecimal(), listOf()),
        ),
        expected = null,
    )

    @Test
    fun `invoke should return Success 100 percent investments without stash`() = `invoke should return Success with filtered items`(
        expenses = listOf(
            GroupedExpenses("December 2025", 0.toBigDecimal(), listOf()),
        ),
        investments = NO_INVESTMENTS,
        savings = NO_SAVINGS,
        expected = null,
    )

    @Test
    fun `invoke should return Success 0 income with stash`() = `invoke should return Success with filtered items`(
        expenses = listOf(
            GroupedExpenses("December 2025", 0.toBigDecimal(), listOf()),
        ),
        expected = null,
    )

    @Test
    fun `invoke should return Success 0 income without stash`() = `invoke should return Success with filtered items`(
        expenses = listOf(
            GroupedExpenses("December 2025", 0.toBigDecimal(), listOf()),
        ),
        investments = NO_INVESTMENTS,
        savings = NO_SAVINGS,
        expected = null,
    )

    @Test
    fun `invoke should return Success 0 percent investments with stash`() = `invoke should return Success with filtered items`(
        expenses = listOf(
            GroupedExpenses("December 2025", 5000.toBigDecimal(), listOf(
                Expense("1", "1", 3500.toBigDecimal(), "1", 1, false, false, 1),
                Expense("2", "2", 1500.toBigDecimal(), "2", 2, false, false, 2),
            )),
        ),
        expected = EXPECTED_FIRE.copy(
            monthlyExpenses = "5000".toBigDecimal(),
            monthlyInvestments = "0".toBigDecimal(),
            annualExpenses = "60000".toBigDecimal(),
            annualInvestments = "0".toBigDecimal(),
            savingsRate = "0.00".toBigDecimal(),
            target = "1650000.00".toBigDecimal(),
            yearsUntilFiRe = "51.84".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success 0 percent investments without stash`() = `invoke should return Success with filtered items`(
        expenses = listOf(
            GroupedExpenses("December 2025", 5000.toBigDecimal(), listOf(
                Expense("1", "1", 3500.toBigDecimal(), "1", 1, false, false, 1),
                Expense("2", "2", 1500.toBigDecimal(), "2", 2, false, false, 2),
            )),
        ),
        investments = NO_INVESTMENTS,
        savings = NO_SAVINGS,
        expected = EXPECTED_FIRE.copy(
            monthlyExpenses = "5000".toBigDecimal(),
            monthlyInvestments = "0".toBigDecimal(),
            annualExpenses = "60000".toBigDecimal(),
            annualInvestments = "0".toBigDecimal(),
            accumulatedStash = "0.00".toBigDecimal(),
            savingsRate = "0.00".toBigDecimal(),
            target = "1650000.00".toBigDecimal(),
            yearsUntilFiRe = "99.99".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success with 2,5 percent withdrawal rate`() = `invoke should return Success with filtered items`(
        withdrawalRate = 2.5.toBigDecimal(),
        expected = EXPECTED_FIRE.copy(
            target = "1100000.00".toBigDecimal(),
            withdrawalRate = "2.5".toBigDecimal(),
            yearsUntilFiRe = "16.85".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success with 5,5 percent withdrawal rate`() = `invoke should return Success with filtered items`(
        withdrawalRate = 5.5.toBigDecimal(),
        expected = EXPECTED_FIRE.copy(
            target = "550000.00".toBigDecimal(),
            withdrawalRate = "5.5".toBigDecimal(),
            yearsUntilFiRe = "10.34".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success with 5 percent return of investments`() = `invoke should return Success with filtered items`(
        returnOfInvestment = 5.toBigDecimal(),
        expected = EXPECTED_FIRE.copy(
            target = "825000.00".toBigDecimal(),
            returnOfInvestment = "5".toBigDecimal(),
            yearsUntilFiRe = "15.84".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success with 9 percent return of investments`() = `invoke should return Success with filtered items`(
        returnOfInvestment = 9.toBigDecimal(),
        expected = EXPECTED_FIRE.copy(
            target = "825000.00".toBigDecimal(),
            returnOfInvestment = "9".toBigDecimal(),
            yearsUntilFiRe = "12.53".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success with 0 percent tax rate`() = `invoke should return Success with filtered items`(
        taxRate = 0.toBigDecimal(),
        expected = EXPECTED_FIRE.copy(
            target = "750000.00".toBigDecimal(),
            taxRate = "0".toBigDecimal(),
            yearsUntilFiRe = "13.05".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke should return Success with 15 percent tax rate`() = `invoke should return Success with filtered items`(
        taxRate = 15.toBigDecimal(),
        expected = EXPECTED_FIRE.copy(
            target = "862500.00".toBigDecimal(),
            taxRate = "15".toBigDecimal(),
            yearsUntilFiRe = "14.38".toBigDecimal(),
        ),
    )

    @Test
    fun `invoke without withdrawalRate should return incomplete fire`() = `invoke should return Success with filtered items`(
        withdrawalRate = null,
        expected = EXPECTED_FIRE_NOT_READY.copy(
            returnOfInvestment = 7.toBigDecimal(),
            taxRate = 10.toBigDecimal(),
        ),
    )

    @Test
    fun `invoke without returnOfInvestment should return incomplete fire`() = `invoke should return Success with filtered items`(
        returnOfInvestment = null,
        expected = EXPECTED_FIRE_NOT_READY.copy(
            withdrawalRate = 4.toBigDecimal(),
            taxRate = 10.toBigDecimal(),
        ),
    )

    @Test
    fun `invoke without taxRate should return incomplete fire`() = `invoke should return Success with filtered items`(
        taxRate = null,
        expected = EXPECTED_FIRE_NOT_READY.copy(
            withdrawalRate = 4.toBigDecimal(),
            returnOfInvestment = 7.toBigDecimal(),
        ),
    )

    private fun `invoke should return Success with filtered items`(
        withdrawalRate: BigDecimal? = 4.toBigDecimal(),
        returnOfInvestment: BigDecimal? = 7.toBigDecimal(),
        taxRate: BigDecimal? = 10.toBigDecimal(),

        budget: List<GroupedBudgetCategories> = BUDGET,
        expenses: List<GroupedExpenses> = EXPENSES,
        investments: GroupedInvestments = INVESTMENTS,
        savings: GroupedSavings = SAVINGS,

        expected: FiRe? = EXPECTED_FIRE,
    ) = runTest(testDispatcher) {
        val withdrawalRateFlow = flowOf(withdrawalRate)
        val returnOfInvestmentFlow = flowOf(returnOfInvestment)
        val taxRateFlow = flowOf(taxRate)

        val dateRangeFlow = flowOf(NO_DATE_RANGE)

        every { observeGroupedBudgetCategoriesMonthlyUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(budget))

        every { observeGroupedExpensesMonthlyUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = eq(dateRangeFlow),
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(expenses))

        every { observeGroupedInvestmentsAllTimeUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = null,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(investments))

        every { observeGroupedSavingsAllTimeUseCase.invoke(
            searchQueryFlow = null,
            dateRangeFlow = null,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(savings))

        val results = observeUseCase(withdrawalRateFlow, returnOfInvestmentFlow, taxRateFlow, dateRangeFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val actual = (first as Async.Success).data
        assertEquals(expected, actual)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        val withdrawalRateFlow = flowOf(4.toBigDecimal())
        val returnOfInvestmentFlow = flowOf(7.toBigDecimal())
        val taxRateFlow = flowOf(10.toBigDecimal())

        val dateRangeFlow = flowOf(NO_DATE_RANGE)

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

        every { observeGroupedInvestmentsAllTimeUseCase.invoke(
             searchQueryFlow = null,
            dateRangeFlow = null,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(NO_INVESTMENTS))

        every { observeGroupedSavingsAllTimeUseCase.invoke(
             searchQueryFlow = null,
            dateRangeFlow = null,
            filterTypeFlow = null,
            sortTypeFlow = null,
            limitFlow = null,
        ) } returns flowOf(Async.Success(NO_SAVINGS))

        val results = observeUseCase(withdrawalRateFlow, returnOfInvestmentFlow, taxRateFlow, dateRangeFlow).toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }

    companion object {

        private val EXPECTED_FIRE = FiRe(
            selection = "December 2025",
            monthlyIncome = "5000".toBigDecimal(),
            monthlyExpenses = "2500".toBigDecimal(),
            monthlyInvestments = "2500".toBigDecimal(),
            annualIncome = "60000".toBigDecimal(),
            annualExpenses = "30000".toBigDecimal(),
            annualInvestments = "30000".toBigDecimal(),
            accumulatedStash = "50000".toBigDecimal(),
            savingsRate = "50.00".toBigDecimal(),
            target = "825000.00".toBigDecimal(),
            withdrawalRate = "4".toBigDecimal(),
            returnOfInvestment = "7".toBigDecimal(),
            taxRate = "10".toBigDecimal(),
            yearsUntilFiRe = "13.96".toBigDecimal(),
        )

        private val EXPECTED_FIRE_NOT_READY = EXPECTED_FIRE.copy(
            savingsRate = null,
            target = null,

            withdrawalRate = null,
            returnOfInvestment = null,
            taxRate = null,

            yearsUntilFiRe = null,
        )

        private val BUDGET = listOf(
            GroupedBudgetCategories(
                group = "December 2025",
                total = 5000.toBigDecimal(),
                items = listOf(
                    BudgetCategory("1", "1", 5000.toBigDecimal()),
                ),
            ),
        )

        private val EXPENSES = listOf(
            GroupedExpenses("December 2025", 2500.toBigDecimal(), listOf(
                Expense("1", "1", 1500.toBigDecimal(), "1", 1, false, false, 1),
                Expense("2", "2", 1000.toBigDecimal(), "2", 2, false, false, 2),
            )),
        )
        
        private val NO_INVESTMENTS = GroupedInvestments("December 2025", "", listOf())

        private val INVESTMENTS = GroupedInvestments("December 2025", "40,000 EUR", listOf(
            Investment("1", "1", 1.toBigDecimal(), 20_000.toBigDecimal(), "1", "EUR", 1, false, 1),
            Investment("2", "2", 1.toBigDecimal(), 10_000.toBigDecimal(), "2", "EUR", 2, false, 2),
            Investment("2", "2", 1.toBigDecimal(), 10_000.toBigDecimal(), "2", "EUR", 2, false, 2),
        ))
        
        private val NO_SAVINGS = GroupedSavings("December 2025", "0.00".toBigDecimal(), listOf())

        private val SAVINGS = GroupedSavings("December 2025", 10_000.toBigDecimal(), listOf(
            Saving("1", "Emergency fund", 7_000.toBigDecimal(), 1, false, 1),
            Saving("2", "Emergency fund", 3_000.toBigDecimal(), 2, false, 2),
        ))

    }

}