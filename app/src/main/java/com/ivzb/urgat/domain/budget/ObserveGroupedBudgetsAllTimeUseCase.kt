package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.investment.Investment.Companion.total
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesAllTimeUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsAllTimeUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsAllTimeUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveGroupedBudgetsAllTimeUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<GroupedBudget>>

}

class DefaultObserveGroupedBudgetsAllTimeUseCase @Inject constructor(
    private val observeGroupedBudgetCategoriesAllTimeUseCase: ObserveGroupedBudgetCategoriesAllTimeUseCase,
    private val observeGroupedExpensesAllTimeUseCase: ObserveGroupedExpensesAllTimeUseCase,
    private val observeGroupedInvestmentsAllTimeUseCase: ObserveGroupedInvestmentsAllTimeUseCase,
    private val observeGroupedSavingsAllTimeUseCase: ObserveGroupedSavingsAllTimeUseCase,
    private val forexRepository: ForexRepository,
): ObserveGroupedBudgetsAllTimeUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<GroupedBudget>> {

        return combine(
            observeGroupedBudgetCategoriesAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
            observeGroupedExpensesAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedInvestmentsAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedSavingsAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        ) { budgetCategoriesAllTimeAsync, expensesAllTimeAsync, investmentsAllTimeAsync, savingsAllTimeAsync ->

            if (budgetCategoriesAllTimeAsync is Async.Success &&
                expensesAllTimeAsync is Async.Success &&
                investmentsAllTimeAsync is Async.Success &&
                savingsAllTimeAsync is Async.Success) {

                val budget = budgetCategoriesAllTimeAsync.data
                val expenses = expensesAllTimeAsync.data
                val investments = investmentsAllTimeAsync.data
                val savings = savingsAllTimeAsync.data

                val budgetCurrency = budget.currency

                GroupedBudget(
                    category = GroupedItems(
                        group = "",
                        total = budget.value,
                        items = listOf(budget),
                    ),
                    totalIncome = budget.value,
                    totalExpenses = expenses.total,
                    totalInvestments = total(investments.items, budgetCurrency, forexRepository::convert),
                    totalSavings = savings.total,
                )
            } else {
                throw Exception("Async.Error")
            }

        }
        .map<GroupedBudget, Async<GroupedBudget>> { Async.Success(it) }
        .catch { e ->
            Tracker.error(e)
            emit(Async.Error)
        }
    }

}
