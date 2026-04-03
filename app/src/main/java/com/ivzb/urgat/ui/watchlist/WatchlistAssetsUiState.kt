package com.ivzb.urgat.ui.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.PagerUiState

data class WatchlistAssetsUiState(
    val watchlistAssets: List<WatchlistAsset> = listOf(),

    override val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = watchlistAssets.isEmpty(),

    override val pages: List<Page> = listOf(),
    override val initialPage: Int = 0,

    override val filteringUiInfo: WatchlistAssetsFilteringUiInfo = WatchlistAssetsFilteringUiInfo()
): PagerUiState

