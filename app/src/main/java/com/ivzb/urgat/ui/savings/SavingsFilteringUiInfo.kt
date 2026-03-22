package com.ivzb.urgat.ui.savings

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class SavingsFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_savings,

    override val emptyLabel: Int = R.string.no_savings,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_savings,
    override val emptyMessage: Int = R.string.no_savings_message,
    override val emptyButtonLabel: Int = R.string.add_saving,

    override val loadingLabel: Int = R.string.loading_savings,

    override val errorLabel: Int = R.string.error_savings,
    override val errorIconRes: Int = R.drawable.ic_error,

    override val searchQuery: String = "",
    override val hasAppliedFilters: Boolean = false,

    val sortMenuItems: List<SavingsSortMenuItem> = listOf(),
    val showSortBadge: Boolean = false,
) : FilteringUiInfo {

    companion object {

        fun from(
            searchQuery: String,
            sortType: SavingsSortType,
            sortMenuItems: List<SavingsSortMenuItem>,
        ): SavingsFilteringUiInfo {
            val showSortBadge = sortType != SavingsSortType.BY_DATE
            val hasAppliedFilters = searchQuery.isNotEmpty() || showSortBadge

            return SavingsFilteringUiInfo(
                currentFilteringLabel = R.string.all_savings,

                emptyLabel = when {
                    hasAppliedFilters -> R.string.no_savings_search
                    else -> R.string.no_savings
                },
                emptyMessage = when {
                    hasAppliedFilters -> R.string.no_savings_search_message
                    else -> R.string.no_savings_message
                },
                emptyButtonLabel = when {
                    hasAppliedFilters -> R.string.reset_filters
                    else -> R.string.add_saving
                },

                searchQuery = searchQuery,
                hasAppliedFilters = hasAppliedFilters,

                sortMenuItems = sortMenuItems,
                showSortBadge = showSortBadge,
            )
        }
    }
}
