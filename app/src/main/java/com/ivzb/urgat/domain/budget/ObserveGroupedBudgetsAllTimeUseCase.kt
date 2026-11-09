package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesAllTimeUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsAllTimeUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsAllTimeUseCase
import com.ivzb.urgat.domain.trades.ObserveGroupedTradesAllTimeUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import com.ivzb.urgat.util.combineAsync
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

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
    private val observeGroupedTradesAllTimeUseCase: ObserveGroupedTradesAllTimeUseCase,
    private val observeGroupedSavingsAllTimeUseCase: ObserveGroupedSavingsAllTimeUseCase,
    private val forexRepository: ForexRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
): ObserveGroupedBudgetsAllTimeUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<GroupedBudget>> = combineAsync(
        observeGroupedBudgetCategoriesAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedExpensesAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeGroupedInvestmentsAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeGroupedTradesAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeGroupedSavingsAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
    ) { budget, expenses, investments, trades, savings ->

        val budgetCurrency = budget.currency

        GroupedBudget(
            category = GroupedItems(
                group = "",
                total = budget.value,
                items = listOf(budget),
            ),
            currency = budget.currency,
            totalIncome = budget.value,
            totalExpenses = Expense.total(expenses.items, budgetCurrency, forexRepository::convert),
            totalInvestments = Investment.total(investments.items, budgetCurrency, forexRepository::convert),
            totalTrades = Trade.total(trades.items, budgetCurrency, forexRepository::convert),
            totalSavings = Saving.total(savings.items, budgetCurrency, forexRepository::convert),
        )
    }
        .catch { e ->
            Tracker.error(e)
            emit(Async.Error)
        }
        .flowOn(dispatcher)

}
