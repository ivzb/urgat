package com.ivzb.urgat.ui.trades

sealed class TradesIntent {
    data class SearchQueryChanged(val query: String): TradesIntent()
    data class SetSorting(val sortType: TradesSortType): TradesIntent()
    object ResetFilters: TradesIntent()
}

