package com.ivzb.urgat.ui.finance

sealed class FinanceIntent {
    data class ItemsLimitChanged(val itemsLimit: Int): FinanceIntent()
    data class SearchQueryChanged(val query: String): FinanceIntent()
    data class FabActionClick(val itemId: FinanceFabActionId): FinanceIntent()
    object EventConsumed: FinanceIntent()
}