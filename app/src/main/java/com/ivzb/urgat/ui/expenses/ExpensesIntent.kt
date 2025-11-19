package com.ivzb.urgat.ui.expenses

sealed class ExpensesIntent {
    data class SearchQueryChanged(val query: String): ExpensesIntent()
    data class SetFiltering(val filter: ExpensesFilterMenuItem): ExpensesIntent()
}