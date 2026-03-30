package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType

fun sortWatchlistAssets(items: List<WatchlistAsset>, sortType: WatchlistAssetsSortType): List<WatchlistAsset> {
    return when (sortType) {
        WatchlistAssetsSortType.BY_DATE -> items.sortedByDescending { it.createdAt }
        WatchlistAssetsSortType.BY_NAME -> items.sortedBy { it.name.lowercase() }
    }
}

