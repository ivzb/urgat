package com.ivzb.urgat.ui.savings

sealed class SavingsIntent {
    data class SearchQueryChanged(val query: String): SavingsIntent()
    data class SetSorting(val sortType: SavingsSortType): SavingsIntent()
    object ResetFilters: SavingsIntent()
}