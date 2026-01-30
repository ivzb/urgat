package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.domain.budget.ObserveGroupedBudgetCategoriesMonthlyUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsMonthlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsAllTimeUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsMonthlyUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsAllTimeUseCase
import com.ivzb.urgat.util.Assert
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface ObserveFiReUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<Unit>? = null,
        sortTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<FiRe>>

}

class DefaultObserveFiReUseCase @Inject constructor(
    private val observeGroupedBudgetCategoriesMonthlyUseCase: ObserveGroupedBudgetCategoriesMonthlyUseCase,
    private val observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase,
    private val observeGroupedInvestmentsMonthlyUseCase: ObserveGroupedInvestmentsMonthlyUseCase,
    private val observeGroupedSavingsMonthlyUseCase: ObserveGroupedSavingsMonthlyUseCase,
    private val observeGroupedInvestmentsAllTimeUseCase: ObserveGroupedInvestmentsAllTimeUseCase,
    private val observeGroupedSavingsAllTimeUseCase: ObserveGroupedSavingsAllTimeUseCase,
): ObserveFiReUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<FiRe>> {

        return com.ivzb.urgat.util.combine(
            observeGroupedBudgetCategoriesMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedExpensesMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedInvestmentsMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedInvestmentsAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedSavingsMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedSavingsAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        ) { budgetCategoriesMonthlyAsync, expensesMonthlyAsync, investmentsMonthlyAsync, investmentsAllTimeAsync, savingsMonthlyAsync, savingsAllTimeAsync ->

            if (budgetCategoriesMonthlyAsync is Async.Success &&
                expensesMonthlyAsync is Async.Success &&
                investmentsMonthlyAsync is Async.Success &&
                investmentsAllTimeAsync is Async.Success &&
                savingsMonthlyAsync is Async.Success &&
                savingsAllTimeAsync is Async.Success) {

                val budgets = budgetCategoriesMonthlyAsync.data
                val expenses = expensesMonthlyAsync.data
                val investments = investmentsMonthlyAsync.data
                val savings = savingsMonthlyAsync.data

                if (budgets.size > 1) {
                    Assert.fail("Unexpected to find more han one monthly budgets for Fi/Re.")
                }

                if (expenses.size > 1) {
                    Assert.fail("Unexpected to find more han one monthly expenses for Fi/Re.")
                }

                if (investments.size > 1) {
                    Assert.fail("Unexpected to find more han one monthly investments for Fi/Re.")
                }

                if (savings.size > 1) {
                    Assert.fail("Unexpected to find more han one monthly savings for Fi/Re.")
                }

                val fire = FiRe(
                    lastMonthIncome = budgets.sumOf { it.total } / budgets.size.coerceAtLeast(1).toBigDecimal(),
                    lastMonthExpenses = expenses.sumOf { it.total } / expenses.size.coerceAtLeast(1).toBigDecimal(),
                    lastMonthInvestments = investments.sumOf { it.items.sumOf { it.marketValue } } / investments.size.coerceAtLeast(1).toBigDecimal(),
                    lastMonthSavings = savings.sumOf { it.total } / savings.size.coerceAtLeast(1).toBigDecimal(),

                    allTimeInvestments = investmentsAllTimeAsync.data.items.sumOf { it.marketValue },
                    allTimeSavings = savingsAllTimeAsync.data.total
                )

                Async.Success(fire)
            } else {
                Async.Error
            }
        }
        .catch { e ->
            Tracker.error(e)
            emit(Async.Error)
        }
    }

}