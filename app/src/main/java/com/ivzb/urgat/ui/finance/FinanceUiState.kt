package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo

data class FinanceUiState(
    val fabActions: List<FinanceFabAction> = FINANCE_FAB_ACTIONS,

    val expenses: List<Expense> = emptyList(),
    val expensesFilteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo(),

    val investments: List<Investment> = emptyList(),
    val investmentsFilteringUiInfo: InvestmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: FilteringUiInfo = FinanceFilteringUiInfo(),

    val uiEvent: FinanceUiEvent? = null,
)