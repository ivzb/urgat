package com.ivzb.urgat.domain.finance

import com.ivzb.urgat.domain.budget.ObserveBudgetsFilteringUseCase
import com.ivzb.urgat.domain.budget.ObserveBudgetsSortingUseCase
import com.ivzb.urgat.domain.budget.ObserveBudgetsUseCase
import com.ivzb.urgat.domain.budget.ObserveReportBudgetUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesFilteringUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesSortingUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.fire.ObserveFiReUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsMonthlyUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsFilteringUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsSortingUseCase
import com.ivzb.urgat.domain.saving.ObserveGroupedSavingsMonthlyUseCase
import com.ivzb.urgat.domain.saving.ObserveSavingsSortingUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.budgets.BudgetsUiState
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.finance.FinanceFilteringUiInfo
import com.ivzb.urgat.ui.finance.FinanceUiState
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.ui.savings.SavingsFilteringUiInfo
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.ui.savings.SavingsUiState
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.combineUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

interface ObserveFinanceUiStateUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,

        expensesFilterTypeFlow: Flow<ExpensesFilterType>,
        expensesSortTypeFlow: Flow<ExpensesSortType>,

        investmentsFilterTypeFlow: Flow<String>,
        investmentsSortTypeFlow: Flow<InvestmentsSortType>,

        savingsSortTypeFlow: Flow<SavingsSortType>,

        budgetsFilterTypeFlow: Flow<BudgetsFilterType>,
        budgetsSortTypeFlow: Flow<BudgetsSortType>,
        budgetDateRangeFlow: Flow<LongRange>,

        withdrawalRateFlow: Flow<BigDecimal?>,
        returnOfInvestmentFlow: Flow<BigDecimal?>,
        taxRateFlow: Flow<BigDecimal?>,

        fireDateRangeFlow: Flow<LongRange>,
    ): Flow<Async<FinanceUiState>>

}

class DefaultObserveFinanceUiStateUseCase @Inject constructor(
    private val observeGroupedExpensesMonthlyUseCase: ObserveGroupedExpensesMonthlyUseCase,
    private val observeExpensesFilteringUseCase: ObserveExpensesFilteringUseCase,
    private val observeExpensesSortingUseCase: ObserveExpensesSortingUseCase,
    private val observeGroupedInvestmentsMonthlyUseCase: ObserveGroupedInvestmentsMonthlyUseCase,
    private val observeInvestmentsFilteringUseCase: ObserveInvestmentsFilteringUseCase,
    private val observeInvestmentsSortingUseCase: ObserveInvestmentsSortingUseCase,
    private val observeGroupedSavingsMonthlyUseCase: ObserveGroupedSavingsMonthlyUseCase,
    private val observeSavingsSortingUseCase: ObserveSavingsSortingUseCase,
    private val observeBudgetsUseCase: ObserveBudgetsUseCase,
    private val observeBudgetsFilteringUseCase: ObserveBudgetsFilteringUseCase,
    private val observeBudgetsSortingUseCase: ObserveBudgetsSortingUseCase,
    private val observeReportBudgetUseCase: ObserveReportBudgetUseCase,
    private val observeFiReUseCase: ObserveFiReUseCase,
) : ObserveFinanceUiStateUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,

        expensesFilterTypeFlow: Flow<ExpensesFilterType>,
        expensesSortTypeFlow: Flow<ExpensesSortType>,

        investmentsFilterTypeFlow: Flow<String>,
        investmentsSortTypeFlow: Flow<InvestmentsSortType>,

        savingsSortTypeFlow: Flow<SavingsSortType>,

        budgetsFilterTypeFlow: Flow<BudgetsFilterType>,
        budgetsSortTypeFlow: Flow<BudgetsSortType>,
        budgetDateRangeFlow: Flow<LongRange>,

        withdrawalRateFlow: Flow<BigDecimal?>,
        returnOfInvestmentFlow: Flow<BigDecimal?>,
        taxRateFlow: Flow<BigDecimal?>,

        fireDateRangeFlow: Flow<LongRange>,
    ): Flow<Async<FinanceUiState>> {

        val expensesFlow = observeExpensesLightUiState(searchQueryFlow, expensesFilterTypeFlow, expensesSortTypeFlow)
        val investmentsFlow = observeInvestmentsLightUiState(searchQueryFlow, investmentsFilterTypeFlow, investmentsSortTypeFlow)
        val savingsFlow = observeSavingsLightUiState(searchQueryFlow, savingsSortTypeFlow)
        val budgetsFlow = observeBudgetsLightUiState(searchQueryFlow, budgetsFilterTypeFlow, budgetsSortTypeFlow)
        val reportBudgetFlow = observeReportBudgetUseCase(budgetDateRangeFlow)
        val fireFlow = observeFiReUseCase(withdrawalRateFlow, returnOfInvestmentFlow, taxRateFlow, fireDateRangeFlow)

        return combineUiState(
            expensesFlow,
            investmentsFlow,
            savingsFlow,
            budgetsFlow,
            reportBudgetFlow,
            fireFlow,
            searchQueryFlow.map { Async.Success(it) },

            onLoading = {
                FinanceUiState(isLoading = true)
            },
            onError = {
                FinanceUiState(hasError = true)
            },
            onSuccess = { expensesUiState, investmentsUiState, savingsUiState, budgetsUiState, reportBudget, fire, searchQuery ->
                FinanceUiState(
                    expensesUiState = expensesUiState,
                    investmentsUiState = investmentsUiState,
                    savingsUiState = savingsUiState,
                    budgetsUiState = budgetsUiState,
                    reportBudget = reportBudget,
                    fire = fire,
                    isLoading = false,
                    filteringUiInfo = FinanceFilteringUiInfo(
                        searchQuery = searchQuery,
                    ),
                )
            }
        ).map { Async.Success(it) }
    }

    private fun observeExpensesLightUiState(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>,
        sortTypeFlow: Flow<ExpensesSortType>,
    ): Flow<Async<ExpensesUiState>> = combineUiState(
        observeGroupedExpensesMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeExpensesFilteringUseCase(),
        observeExpensesSortingUseCase(),
        combine(searchQueryFlow, filterTypeFlow, sortTypeFlow, ::Triple).map { Async.Success(it) },

        onLoading = {
            ExpensesUiState(isLoading = true)
        },
        onError = {
            ExpensesUiState(hasError = true)
        },
        onSuccess = { expenses, filtering, sorting, (searchQuery, filterType, sortType) ->
            ExpensesUiState(
                expenses = expenses,
                filteringUiInfo = ExpensesFilteringUiInfo.from(
                    searchQuery = searchQuery,
                    filterType = filterType,
                    filterMenuItems = filtering,
                    sortType = sortType,
                    sortMenuItems = sorting,
                ),
            )
        }
    ).map { Async.Success(it) }

    private fun observeInvestmentsLightUiState(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>,
        sortTypeFlow: Flow<InvestmentsSortType>,
    ): Flow<Async<InvestmentsUiState>> = combineUiState(
        observeGroupedInvestmentsMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeInvestmentsFilteringUseCase(),
        observeInvestmentsSortingUseCase(),
        combine(searchQueryFlow, filterTypeFlow, sortTypeFlow, ::Triple).map { Async.Success(it) },

        onLoading = {
            InvestmentsUiState(isLoading = true)
        },
        onError = {
            InvestmentsUiState(hasError = true)
        },
        onSuccess = { investments, filtering, sorting, (searchQuery, filterType, sortType) ->
            InvestmentsUiState(
                investments = investments,
                filteringUiInfo = InvestmentsFilteringUiInfo.from(
                    searchQuery = searchQuery,
                    filterType = filterType,
                    filterMenuItems = filtering,
                    sortType = sortType,
                    sortMenuItems = sorting,
                ),
            )
        }
    ).map { Async.Success(it) }

    private fun observeSavingsLightUiState(
        searchQueryFlow: Flow<String>,
        sortTypeFlow: Flow<SavingsSortType>,
    ): Flow<Async<SavingsUiState>> = combineUiState(
        observeGroupedSavingsMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeSavingsSortingUseCase(),
        combine(searchQueryFlow, sortTypeFlow, ::Pair).map { Async.Success(it) },

        onLoading = {
            SavingsUiState(isLoading = true)
        },
        onError = {
            SavingsUiState(hasError = true)
        },
        onSuccess = { savings, sorting, (searchQuery, sortType) ->
            SavingsUiState(
                savings = savings,
                filteringUiInfo = SavingsFilteringUiInfo.from(
                    searchQuery = searchQuery,
                    sortType = sortType,
                    sortMenuItems = sorting,
                ),
            )
        }
    ).map { Async.Success(it) }

    private fun observeBudgetsLightUiState(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<BudgetsFilterType>,
        sortTypeFlow: Flow<BudgetsSortType>,
    ): Flow<Async<BudgetsUiState>> = combineUiState(
        observeBudgetsUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeBudgetsFilteringUseCase(),
        observeBudgetsSortingUseCase(),
        combine(searchQueryFlow, filterTypeFlow, sortTypeFlow, ::Triple).map { Async.Success(it) },

        onLoading = {
            BudgetsUiState(isLoading = true)
        },
        onError = {
            BudgetsUiState(hasError = true)
        },
        onSuccess = { budgets, filtering, sorting, (searchQuery, filterType, sortType) ->
            BudgetsUiState(
                budgets = budgets,
                filteringUiInfo = BudgetsFilteringUiInfo.from(
                    searchQuery = searchQuery,
                    filterType = filterType,
                    filterMenuItems = filtering,
                    sortType = sortType,
                    sortMenuItems = sorting,
                ),
            )
        }
    ).map { Async.Success(it) }

}

