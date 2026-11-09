package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesYearlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsYearlyUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsYearlyUseCase
import com.ivzb.urgat.domain.trades.ObserveGroupedTradesYearlyUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import com.ivzb.urgat.util.combineAsync
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

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
    private val observeGroupedTradesYearlyUseCase: ObserveGroupedTradesYearlyUseCase,
    private val observeGroupedSavingsYearlyUseCase: ObserveGroupedSavingsYearlyUseCase,
    private val forexRepository: ForexRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
): ObserveGroupedBudgetsYearlyUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedBudget>>> = combineAsync(
        observeGroupedBudgetCategoriesYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedExpensesYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeGroupedInvestmentsYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeGroupedTradesYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeGroupedSavingsYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
    ) { budgetCategoriesYearly, expensesYearly, investmentsYearly, tradesYearly, savingsYearly ->

        budgetCategoriesYearly.mapIndexed { index, category ->
            val expenses = expensesYearly.getOrNull(index)
            val investments = investmentsYearly.getOrNull(index)
            val trades = tradesYearly.getOrNull(index)
            val savings = savingsYearly.getOrNull(index)

            val budgetCurrency = category.items.first().currency

            GroupedBudget(
                category = category,
                currency = budgetCurrency,
                totalIncome = category.total,
                totalExpenses = Expense.total(expenses?.items, budgetCurrency, forexRepository::convert),
                totalInvestments = Investment.total(investments?.items, budgetCurrency, forexRepository::convert),
                totalTrades = Trade.total(trades?.items, budgetCurrency, forexRepository::convert),
                totalSavings = Saving.total(savings?.items, budgetCurrency, forexRepository::convert),
            )
        }
    }
        .catch { e ->
            Tracker.error(e)
            emit(Async.Error)
        }
        .flowOn(dispatcher)

}
