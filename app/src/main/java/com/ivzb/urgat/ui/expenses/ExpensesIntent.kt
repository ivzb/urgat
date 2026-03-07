package com.ivzb.urgat.ui.expenses

import com.ivzb.urgat.ui.finance.FinanceIntent

sealed class ExpensesIntent {
    data class SearchQueryChanged(val query: String): ExpensesIntent()
    data class SetFiltering(val filterType: ExpensesFilterType): ExpensesIntent()
    data class SetSorting(val sortType: ExpensesSortType): ExpensesIntent()
    object ResetFilters: ExpensesIntent()
}