package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.screens.navigation.NavigationUiState
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo

data class FinanceUiState(
    override val fabActions: List<FinanceFabAction> = FINANCE_FAB_ACTIONS,

    val expenses: List<Expense> = emptyList(),
    val expensesFilteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo(),

    val investments: List<Investment> = emptyList(),
    val investmentsFilteringUiInfo: InvestmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = false,

    override val filteringUiInfo: FilteringUiInfo = FinanceFilteringUiInfo(),

    val uiEvent: FinanceUiEvent? = null,
): NavigationUiState<FinanceFabActionId>