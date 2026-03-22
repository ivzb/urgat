package com.ivzb.urgat.ui.investments

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class InvestmentsFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_investments,

    override val emptyLabel: Int = R.string.no_investments,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_investments,
    override val emptyMessage: Int = R.string.no_investments_message,
    override val emptyButtonLabel: Int = R.string.add_investment,

    override val loadingLabel: Int = R.string.loading_investments,

    override val errorLabel: Int = R.string.error_investments,
    override val errorIconRes: Int = R.drawable.ic_error,

    override val searchQuery: String = "",
    override val hasAppliedFilters: Boolean = false,

    val filterMenuItems: List<InvestmentsFilterMenuItem> = listOf(),
    val showFilterBadge: Boolean = false,

    val sortMenuItems: List<InvestmentsSortMenuItem> = listOf(),
    val showSortBadge: Boolean = false,
) : FilteringUiInfo {

    companion object {

        fun from(
            searchQuery: String,
            filterType: String,
            filterMenuItems: List<InvestmentsFilterMenuItem>,
            sortType: InvestmentsSortType,
            sortMenuItems: List<InvestmentsSortMenuItem>,
        ): InvestmentsFilteringUiInfo {
            val showFilterBadge = filterType != InvestmentsFilterType_All
            val showSortBadge = sortType != InvestmentsSortType.BY_DATE
            val hasAppliedFilters = searchQuery.isNotEmpty() || showFilterBadge || showSortBadge

            return InvestmentsFilteringUiInfo(
                currentFilteringLabel = R.string.all_investments,

                emptyLabel = when {
                    hasAppliedFilters -> R.string.no_investments_search
                    else -> R.string.no_investments
                },
                emptyMessage = when {
                    hasAppliedFilters -> R.string.no_investments_search_message
                    else -> R.string.no_investments_message
                },
                emptyButtonLabel = when {
                    hasAppliedFilters -> R.string.reset_filters
                    else -> R.string.add_investment
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
