package com.ivzb.urgat.ui.finance

sealed class FinanceUiEvent {
    object AddExpense : FinanceUiEvent()
    object AddInvestment : FinanceUiEvent()
    object AddTrade: FinanceUiEvent()
    object AddSaving : FinanceUiEvent()
    object AddBudget: FinanceUiEvent()
    object AddWatchlistAsset: FinanceUiEvent()
}
