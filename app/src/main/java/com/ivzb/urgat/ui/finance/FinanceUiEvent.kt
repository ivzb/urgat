package com.ivzb.urgat.ui.finance

sealed class FinanceUiEvent {
    object AddExpense : FinanceUiEvent()
    object AddInvestment : FinanceUiEvent()
    object AddBudget: FinanceUiEvent()
}
