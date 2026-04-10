package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsMonthlyUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsMonthlyUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface ObserveGroupedBudgetsMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedBudget>>>

}

class DefaultObserveGroupedBudgetsMonthlyUseCase @Inject constructor(
    private val observeGroupedBudgetCategoriesMonthlyUseCase: ObserveGroupedBudgetCategoriesMonthlyUseCase,
    private val observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase,
    private val observeGroupedInvestmentsMonthlyUseCase: ObserveGroupedInvestmentsMonthlyUseCase,
    private val observeGroupedSavingsMonthlyUseCase: ObserveGroupedSavingsMonthlyUseCase,
    private val forexRepository: ForexRepository,
): ObserveGroupedBudgetsMonthlyUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedBudget>>> {

        return combine(
            observeGroupedBudgetCategoriesMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
            observeGroupedExpensesMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedInvestmentsMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedSavingsMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        ) { budgetCategoriesMonthlyAsync, expensesMonthlyAsync, investmentsMonthlyAsync, savingsMonthlyAsync ->

            if (budgetCategoriesMonthlyAsync is Async.Success &&
                expensesMonthlyAsync is Async.Success &&
                investmentsMonthlyAsync is Async.Success &&
                savingsMonthlyAsync is Async.Success) {

                val monthlyBudgets = budgetCategoriesMonthlyAsync.data.mapIndexed { index, category ->
                    val expenses = expensesMonthlyAsync.data.getOrNull(index)
                    val investments = investmentsMonthlyAsync.data.getOrNull(index)
                    val savings = savingsMonthlyAsync.data.getOrNull(index)

                    val budgetCurrency = category.items.first().currency

                    GroupedBudget(
                        category = category,
                        currency = budgetCurrency,
                        totalIncome = category.total,
                        totalExpenses = Expense.total(expenses?.items, budgetCurrency, forexRepository::convert),
                        totalInvestments = Investment.total(investments?.items, budgetCurrency, forexRepository::convert),
                        totalSavings = Saving.total(savings?.items, budgetCurrency, forexRepository::convert),
                    )
                }

                Async.Success(monthlyBudgets)
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
