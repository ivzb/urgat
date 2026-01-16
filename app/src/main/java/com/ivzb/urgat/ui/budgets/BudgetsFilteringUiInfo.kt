package com.ivzb.urgat.ui.budgets

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class BudgetsFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_budgets,

    override val emptyLabel: Int = R.string.no_budgets,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_budgets,
    val emptyMessage: Int = R.string.no_budgets_message,
    val emptyButtonLabel: Int = R.string.add_budget,

    override val loadingLabel: Int = R.string.loading_budgets,

    override val errorLabel: Int = R.string.error_budgets,
    override val errorIconRes: Int = R.drawable.ic_error,

    val filterMenuItems: List<BudgetsFilterMenuItem> = listOf(),
    val sortMenuItems: List<BudgetsSortMenuItem> = listOf(),
) : FilteringUiInfo
