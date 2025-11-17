package com.ivzb.urgat.ui.investments

import com.ivzb.urgat.ui.components.FilterMenuItem

sealed class InvestmentsIntent {
    data class SearchQueryChanged(val query: String): InvestmentsIntent()
    data class SetFiltering(val filter: FilterMenuItem): InvestmentsIntent()
    data class HeaderItemClick(val item: InvestmentsHeaderItem): InvestmentsIntent()
    object EventConsumed: InvestmentsIntent()
}
