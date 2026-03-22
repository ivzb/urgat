package com.ivzb.urgat.ui.budgets

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class BudgetsFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_budgets,

    override val emptyLabel: Int = R.string.no_budgets,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_budgets,
    override val emptyMessage: Int = R.string.no_budgets_message,
    override val emptyButtonLabel: Int = R.string.add_budget,

    override val loadingLabel: Int = R.string.loading_budgets,

    override val errorLabel: Int = R.string.error_budgets,
    override val errorIconRes: Int = R.drawable.ic_error,

    override val searchQuery: String = "",
    override val hasAppliedFilters: Boolean = false,

    val filterMenuItems: List<BudgetsFilterMenuItem> = listOf(),
    val showFilterBadge: Boolean = false,

    val sortMenuItems: List<BudgetsSortMenuItem> = listOf(),
    val showSortBadge: Boolean = false,
) : FilteringUiInfo {

    companion object {

        fun from(
            searchQuery: String,
            filterType: BudgetsFilterType,
            filterMenuItems: List<BudgetsFilterMenuItem>,
            sortType: BudgetsSortType,
            sortMenuItems: List<BudgetsSortMenuItem>,
        ): BudgetsFilteringUiInfo {
            val showFilterBadge = filterType != BudgetsFilterType.ALL_BUDGETS
            val showSortBadge = sortType != BudgetsSortType.BY_DATE
            val hasAppliedFilters = searchQuery.isNotEmpty() || showFilterBadge || showSortBadge

            return BudgetsFilteringUiInfo(
                currentFilteringLabel = R.string.all_budgets,

                emptyLabel = when {
                    hasAppliedFilters -> R.string.no_budgets_search
                    else -> R.string.no_budgets
                },
                emptyMessage = when {
                    hasAppliedFilters -> R.string.no_budgets_search_message
                    else -> R.string.no_budgets_message
                },
                emptyButtonLabel = when {
                    hasAppliedFilters -> R.string.reset_filters
                    else -> R.string.add_budget
                },

                searchQuery = searchQuery,
                hasAppliedFilters = hasAppliedFilters,

                filterMenuItems = filterMenuItems,
                showFilterBadge = showFilterBadge,

                sortMenuItems = sortMenuItems,
                showSortBadge = showSortBadge,
            )
        }
    }
}
