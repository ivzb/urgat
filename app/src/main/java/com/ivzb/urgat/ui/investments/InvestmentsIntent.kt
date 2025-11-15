package com.ivzb.urgat.ui.investments

import com.ivzb.urgat.ui.expenses.ExpensesIntent

sealed class InvestmentsIntent {
    data class SearchQueryChanged(val query: String): InvestmentsIntent()
    data class SetFiltering(val filterType: InvestmentsFilterType): InvestmentsIntent()
    data class HeaderItemClick(val item: InvestmentsHeaderItem): InvestmentsIntent()
    object EventConsumed: InvestmentsIntent()
}
