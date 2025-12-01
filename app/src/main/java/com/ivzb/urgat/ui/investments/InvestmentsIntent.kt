package com.ivzb.urgat.ui.investments

sealed class InvestmentsIntent {
    data class SearchQueryChanged(val query: String): InvestmentsIntent()
    data class SetFiltering(val filter: InvestmentsFilterMenuItem): InvestmentsIntent()
}
