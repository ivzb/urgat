package com.ivzb.urgat.ui.watchlist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.ivzb.urgat.R
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.util.annotateFinancePnL
import com.ivzb.urgat.util.annotateWatchlistAsset
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.formatCurrency

@Composable
fun TransactionsPage(
    watchlistAssets: List<WatchlistAsset>,
    onWatchlistAssetClick: (WatchlistAsset) -> Unit,

    header: (@Composable () -> Unit)? = null,

    listState: LazyListState,
    onListStateChanged: (LazyListState) -> Unit,
) {

    LaunchedEffect(Unit) {
        onListStateChanged(listState)
    }

    LazyColumn(
        contentPadding = PaddingValues(
            horizontal = dimensionResource(R.dimen.content_padding_horizontal),
            vertical = dimensionResource(R.dimen.content_padding_vertical),
        ),
        state = listState,
        modifier = Modifier
            .fillMaxSize(),
    ) {

        item {
            header?.invoke()
        }

        items(
            items = watchlistAssets,
            key = { asset -> asset.id },
        ) { asset ->

            WatchlistAssetItem(
                asset = asset,
                onWatchlistAssetClick = onWatchlistAssetClick
            )
        }
    }
}

@Composable
fun WatchlistAssetItem(
    asset: WatchlistAsset,
    onWatchlistAssetClick: (WatchlistAsset) -> Unit
) {
    ListItem(
        textLeft = asset.name.uppercase(),
        subTextLeft = asset.description ?: "",

        textRight = "${asset.marketPrice?.format()} ${asset.currency?.formatCurrency()}",
        subTextRight = annotateWatchlistAsset(asset.priceChange, asset.priceChangePercentage),

        onClick = { onWatchlistAssetClick(asset) }
    )
}

