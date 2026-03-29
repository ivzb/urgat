package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.investment.Investment.Companion.total
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsMonthlyUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsMonthlyUseCase
import com.ivzb.urgat.util.Assert
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import javax.inject.Inject

interface ObserveReportBudgetUseCase {

    operator fun invoke(
        dateRangeFlow: Flow<LongRange>?,
    ): Flow<Async<GroupedBudget?>>

}

class DefaultObserveReportBudgetUseCase @Inject constructor(
    private val observeGroupedBudgetCategoriesMonthlyUseCase: ObserveGroupedBudgetCategoriesMonthlyUseCase,
    private val observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase,
    private val observeGroupedInvestmentsMonthlyUseCase: ObserveGroupedInvestmentsMonthlyUseCase,
    private val observeGroupedSavingsMonthlyUseCase: ObserveGroupedSavingsMonthlyUseCase,
    private val forexRepository: ForexRepository,
): ObserveReportBudgetUseCase {

    override fun invoke(
        dateRangeFlow: Flow<LongRange>?,
    ): Flow<Async<GroupedBudget?>> {

        return combine(
            observeGroupedBudgetCategoriesMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedExpensesMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedInvestmentsMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
            observeGroupedSavingsMonthlyUseCase(searchQueryFlow = null, dateRangeFlow = dateRangeFlow, filterTypeFlow = null, sortTypeFlow = null, limitFlow = null),
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
                        totalIncome = category.total,
                        totalExpenses = expenses?.total ?: BigDecimal.ZERO,
                        totalInvestments = total(investments?.items, budgetCurrency, forexRepository::convert),
                        totalSavings = savings?.total ?: BigDecimal.ZERO,
                    )
                }

                if (monthlyBudgets.size > 1) {
                    Assert.fail("Unexpected to find more han one monthly budget.")
                }

                Async.Success(monthlyBudgets.firstOrNull())
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
