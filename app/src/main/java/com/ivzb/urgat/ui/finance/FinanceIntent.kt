package com.ivzb.urgat.ui.finance

sealed class FinanceIntent {
    data class ItemsLimitChanged(val itemsLimit: Int): FinanceIntent()
    data class SearchQueryChanged(val query: String): FinanceIntent()
    data class FabActionClick(val itemId: FinanceFabActionId): FinanceIntent()
    data class UpdateFireWithdrawalRate(val withdrawalRate: String): FinanceIntent()
    data class UpdateFireReturnOfInvestment(val returnOfInvestment: String): FinanceIntent()
    data class UpdateFireTaxRate(val taxRate: String): FinanceIntent()
    object EventConsumed: FinanceIntent()
}