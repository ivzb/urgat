package com.ivzb.urgat.ui.watchlist

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class WatchlistAssetsFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.watchlist,

    override val emptyLabel: Int = R.string.no_watchlist_assets,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_savings,
    override val emptyMessage: Int = R.string.no_watchlist_assets_message,
    override val emptyButtonLabel: Int = R.string.add_watchlist_asset,

    override val loadingLabel: Int = R.string.loading_watchlist_assets,

    override val errorLabel: Int = R.string.error_watchlist_assets,
    override val errorIconRes: Int = R.drawable.ic_error,

    override val searchQuery: String = "",
    override val hasAppliedFilters: Boolean = false,

    val sortMenuItems: List<WatchlistAssetsSortMenuItem> = listOf(),
    val showSortBadge: Boolean = false,
) : FilteringUiInfo {

    companion object {

        fun from(
            searchQuery: String,
            sortType: WatchlistAssetsSortType,
            sortMenuItems: List<WatchlistAssetsSortMenuItem>,
        ): WatchlistAssetsFilteringUiInfo {
            val showSortBadge = sortType != WatchlistAssetsSortType.BY_DATE
            val hasAppliedFilters = searchQuery.isNotEmpty() || showSortBadge

            return WatchlistAssetsFilteringUiInfo(
                currentFilteringLabel = R.string.watchlist,

                emptyLabel = when {
                    hasAppliedFilters -> R.string.no_watchlist_assets_search
                    else -> R.string.no_watchlist_assets
                },
                emptyMessage = when {
                    hasAppliedFilters -> R.string.no_watchlist_assets_search_message
                    else -> R.string.no_watchlist_assets_message
                },
                emptyButtonLabel = when {
                    hasAppliedFilters -> R.string.reset_filters
                    else -> R.string.add_watchlist_asset
                },

                searchQuery = searchQuery,
                hasAppliedFilters = hasAppliedFilters,

                sortMenuItems = sortMenuItems,
                showSortBadge = showSortBadge,
            )
        }
    }
}

