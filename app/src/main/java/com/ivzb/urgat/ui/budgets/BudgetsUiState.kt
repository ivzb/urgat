package com.ivzb.urgat.ui.budgets

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState

data class BudgetsUiState(
    val budgets: List<Budget> = emptyList(),

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = budgets.isEmpty(),

    override val pages: List<Page> = BUDGET_PAGES,
    override val initialPage: Int = 0,

    override val filteringUiInfo: BudgetsFilteringUiInfo = BudgetsFilteringUiInfo(),
): PagerUiState