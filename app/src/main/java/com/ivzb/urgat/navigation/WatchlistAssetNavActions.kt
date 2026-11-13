package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditWatchlistAsset.AddEditWatchlistAssetRoute

fun UrgatNavActions.navigateToWatchlistAssets() {
    // TODO
}

fun UrgatNavActions.navigateToAddWatchlistAsset() {
    navigator.navigate(
        AddEditWatchlistAssetRoute(
            id = null,
            titleResId = R.string.add_watchlist_asset,
        )
    )
}

fun UrgatNavActions.navigateToEditWatchlistAsset(watchlistAssetId: String) {
    navigator.navigate(
        AddEditWatchlistAssetRoute(
            id = watchlistAssetId,
            titleResId = R.string.edit_watchlist_asset,
        )
    )
}
