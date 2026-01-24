package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.uiState.NavigationUiState
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.ui.savings.SavingsFilteringUiInfo

data class FinanceUiState(
    override val fabActions: List<FinanceFabAction> = FINANCE_FAB_ACTIONS,

    val expenses: List<Expense> = emptyList(),
    val expensesFilteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo(),

    val investments: List<Investment> = emptyList(),
    val investmentsFilteringUiInfo: InvestmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),

    val savings: List<Saving> = emptyList(),
    val savingsFilteringUiInfo: SavingsFilteringUiInfo = SavingsFilteringUiInfo(),

    val budgets: List<Budget> = emptyList(),
    val budgetsFilteringUiInfo: BudgetsFilteringUiInfo = BudgetsFilteringUiInfo(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = false,

    override val filteringUiInfo: FilteringUiInfo = FinanceFilteringUiInfo(),

    val uiEvent: FinanceUiEvent? = null,
): NavigationUiState<FinanceFabActionId>