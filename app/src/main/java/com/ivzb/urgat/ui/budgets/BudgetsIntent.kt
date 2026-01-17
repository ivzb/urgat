package com.ivzb.urgat.ui.budgets

sealed class BudgetsIntent {
    data class SearchQueryChanged(val query: String): BudgetsIntent()
    data class SetFiltering(val filterType: BudgetsFilterType): BudgetsIntent()
    data class SetSorting(val sortType: BudgetsSortType): BudgetsIntent()
}
