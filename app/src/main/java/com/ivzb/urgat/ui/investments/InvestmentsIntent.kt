package com.ivzb.urgat.ui.investments

sealed class InvestmentsIntent {
    data class SearchQueryChanged(val query: String): InvestmentsIntent()
    data class SetFiltering(val filterType: String): InvestmentsIntent()
    data class SetSorting(val sortType: InvestmentsSortType): InvestmentsIntent()
    object ResetFilters: InvestmentsIntent()
}
