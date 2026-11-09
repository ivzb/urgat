package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsMonthlyUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsMonthlyUseCase
import com.ivzb.urgat.domain.trades.ObserveGroupedTradesMonthlyUseCase
import com.ivzb.urgat.util.Assert
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import com.ivzb.urgat.util.combineAsync
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

interface ObserveReportBudgetUseCase {

    operator fun invoke(
        dateRangeFlow: Flow<LongRange>?,
    ): Flow<Async<GroupedBudget?>>

}

class DefaultObserveReportBudgetUseCase @Inject constructor(
    private val observeGroupedBudgetCategoriesMonthlyUseCase: ObserveGroupedBudgetCategoriesMonthlyUseCase,
    private val observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase,
    private val observeGroupedInvestmentsMonthlyUseCase: ObserveGroupedInvestmentsMonthlyUseCase,
    private val observeGroupedTradesMonthlyUseCase: ObserveGroupedTradesMonthlyUseCase,
    private val observeGroupedSavingsMonthlyUseCase: ObserveGroupedSavingsMonthlyUseCase,
    private val forexRepository: ForexRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
): ObserveReportBudgetUseCase {

    override fun invoke(
        dateRangeFlow: Flow<LongRange>?,
    ): Flow<Async<GroupedBudget?>> = combineAsync(
        observeGroupedBudgetCategoriesMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeGroupedExpensesMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeGroupedInvestmentsMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeGroupedTradesMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
        observeGroupedSavingsMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
    ) { budgetCategoriesMonthly, expensesMonthly, investmentsMonthly, tradesMonthly, savingsMonthly ->

        val monthlyBudgets = budgetCategoriesMonthly.mapIndexed { index, category ->
            val expenses = expensesMonthly.getOrNull(index)
            val investments = investmentsMonthly.getOrNull(index)
            val trades = tradesMonthly.getOrNull(index)
            val savings = savingsMonthly.getOrNull(index)

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

        if (monthlyBudgets.size > 1) {
            Assert.fail("Unexpected to find more han one monthly budget.")
        }

        monthlyBudgets.firstOrNull()
    }
        .catch { e ->
            Tracker.error(e)
            emit(Async.Error)
        }
        .flowOn(dispatcher)

}
