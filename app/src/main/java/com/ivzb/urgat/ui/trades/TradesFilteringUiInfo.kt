package com.ivzb.urgat.ui.trades

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class TradesFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.transactions,

    override val emptyLabel: Int = R.string.no_trades,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_trades,
    override val emptyMessage: Int = R.string.no_trades_message,
    override val emptyButtonLabel: Int = R.string.add_trade,

    override val loadingLabel: Int = R.string.loading_trades,

    override val errorLabel: Int = R.string.error_trades,
    override val errorIconRes: Int = R.drawable.ic_error,

    override val searchQuery: String = "",
    override val hasAppliedFilters: Boolean = false,

    val filterMenuItems: List<TradesFilterMenuItem> = listOf(),
    val showFilterBadge: Boolean = false,

    val sortMenuItems: List<TradesSortMenuItem> = listOf(),
    val showSortBadge: Boolean = false,
) : FilteringUiInfo {

    companion object {

        fun from(
            searchQuery: String,
            filterType: String,
            filterMenuItems: List<TradesFilterMenuItem>,
            sortType: TradesSortType,
            sortMenuItems: List<TradesSortMenuItem>,
        ): TradesFilteringUiInfo {
            val showFilterBadge = filterType != TradesFilterType_All
            val showSortBadge = sortType != TradesSortType.BY_DATE
            val hasAppliedFilters = searchQuery.isNotEmpty() || showFilterBadge || showSortBadge

            return TradesFilteringUiInfo(
                currentFilteringLabel = R.string.transactions,

                emptyLabel = when {
                    hasAppliedFilters -> R.string.no_trades_search
                    else -> R.string.no_trades
                },
                emptyMessage = when {
                    hasAppliedFilters -> R.string.no_trades_search_message
                    else -> R.string.no_trades_message
                },
                emptyButtonLabel = when {
                    hasAppliedFilters -> R.string.reset_filters
                    else -> R.string.add_trade
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
