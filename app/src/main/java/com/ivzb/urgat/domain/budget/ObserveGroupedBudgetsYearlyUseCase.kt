package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.investment.InvestmentTotal.Companion.formatCostPrice
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesYearlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsYearlyUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsYearlyUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

interface ObserveGroupedBudgetsYearlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedBudget>>>

}

class DefaultObserveGroupedBudgetsYearlyUseCase @Inject constructor(
    private val observeGroupedBudgetCategoriesYearlyUseCase: ObserveGroupedBudgetCategoriesYearlyUseCase,
    private val observeGroupedExpensesYearlyUseCase: ObserveGroupedExpensesYearlyUseCase,
    private val observeGroupedInvestmentsYearlyUseCase: ObserveGroupedInvestmentsYearlyUseCase,
    private val observeGroupedSavingsYearlyUseCase: ObserveGroupedSavingsYearlyUseCase,
): ObserveGroupedBudgetsYearlyUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedBudget>>> {

        return combine(
            observeGroupedBudgetCategoriesYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
            observeGroupedExpensesYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedInvestmentsYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedSavingsYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        ) { budgetCategoriesYearlyAsync, expensesYearlyAsync, investmentsYearlyAsync, savingsYearlyAsync ->

            if (budgetCategoriesYearlyAsync is Async.Success &&
                expensesYearlyAsync is Async.Success &&
                investmentsYearlyAsync is Async.Success &&
                savingsYearlyAsync is Async.Success) {

                val yearlyBudgets = budgetCategoriesYearlyAsync.data.mapIndexed { index, category ->
                    val expenses = expensesYearlyAsync.data.getOrNull(index)
                    val investments = investmentsYearlyAsync.data.getOrNull(index)
                    val savings = savingsYearlyAsync.data.getOrNull(index)

                    GroupedBudget(
                        category = category,
                        totalIncome = category.total,
                        totalExpenses = expenses?.total ?: BigDecimal.ZERO,
                        totalInvestments = investments?.items?.sumOf { it.totalCostPrice } ?: BigDecimal.ZERO,
                        totalInvestmentsFormatted = investments?.total?.formatCostPrice() ?: "",
                        totalSavings = savings?.total ?: BigDecimal.ZERO,
                    )
                }

                yearlyBudgets
            } else {
                throw Exception("Async.Error")
            }

        }
        .map<List<GroupedBudget>, Async<List<GroupedBudget>>> { Async.Success(it) }
        .catch { e ->
            Tracker.error(e)
            emit(Async.Error)
        }
    }

}
