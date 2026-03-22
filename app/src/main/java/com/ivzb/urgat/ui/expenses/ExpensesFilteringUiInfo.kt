package com.ivzb.urgat.ui.expenses

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class ExpensesFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_expenses,

    override val emptyLabel: Int = R.string.no_expenses_label,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_expenses,
    override val emptyMessage: Int = R.string.no_expenses_message,
    override val emptyButtonLabel: Int = R.string.add_expense,

    override val loadingLabel: Int = R.string.loading_expenses,

    override val errorLabel: Int = R.string.error_expenses,
    override val errorIconRes: Int = R.drawable.ic_error,

    override val searchQuery: String = "",
    override val hasAppliedFilters: Boolean = false,

    val filterMenuItems: List<ExpensesFilterMenuItem> = listOf(),
    val showFilterBadge: Boolean = false,

    val sortMenuItems: List<ExpensesSortMenuItem> = listOf(),
    val showSortBadge: Boolean = false,

) : FilteringUiInfo {

    companion object {

        fun from(
            searchQuery: String,
            filterType: ExpensesFilterType,
            filterMenuItems: List<ExpensesFilterMenuItem>,
            sortType: ExpensesSortType,
            sortMenuItems: List<ExpensesSortMenuItem>,
        ): ExpensesFilteringUiInfo {
            val showFilterBadge = filterType != ExpensesFilterType.ALL_EXPENSES
            val showSortBadge = sortType != ExpensesSortType.BY_DATE
            val hasAppliedFilters = searchQuery.isNotEmpty() || showFilterBadge || showSortBadge

            return ExpensesFilteringUiInfo(
                currentFilteringLabel = when (filterType) {
                    ExpensesFilterType.ALL_EXPENSES -> R.string.all_expenses
                    ExpensesFilterType.DEBIT_EXPENSES -> R.string.debit_expenses
                    ExpensesFilterType.COST_EXPENSES -> R.string.cost_expenses
                    ExpensesFilterType.EXCLUDED_FROM_ANALYTICS -> R.string.exclude_from_analytics
                },

                emptyLabel = when {
                    hasAppliedFilters -> R.string.no_expenses_search
                    filterType == ExpensesFilterType.DEBIT_EXPENSES -> R.string.no_debit_expenses
                    filterType == ExpensesFilterType.COST_EXPENSES -> R.string.no_cost_expenses
                    filterType == ExpensesFilterType.EXCLUDED_FROM_ANALYTICS -> R.string.no_excluded_from_analytics_expenses
                    else -> R.string.no_expenses
                },
                emptyMessage = when {
                    hasAppliedFilters -> R.string.no_expenses_search_message
                    else -> R.string.no_expenses_message
                },
                emptyButtonLabel = when {
                    hasAppliedFilters -> R.string.reset_filters
                    else -> R.string.add_expense
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
