package com.ivzb.urgat.ui.budgets

sealed class BudgetsIntent {
    data class SearchQueryChanged(val query: String): BudgetsIntent()
}
