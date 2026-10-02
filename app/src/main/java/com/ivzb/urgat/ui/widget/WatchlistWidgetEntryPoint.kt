package com.ivzb.urgat.ui.widget

import android.content.Context
import com.ivzb.urgat.data.finance.FinanceRepository
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.domain.watchlist.ObserveWatchlistAssetsUseCase
import com.ivzb.urgat.domain.widget.RefreshQuotesUseCase
import com.ivzb.urgat.util.Async
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow

/**
 * Reaches into the Hilt graph by hand, the same way [NetWorthWidgetEntryPoint] does and for the
 * very same reason - see its documentation.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WatchlistWidgetEntryPoint {

    fun observeWatchlistAssetsUseCase(): ObserveWatchlistAssetsUseCase

    fun refreshQuotesUseCase(): RefreshQuotesUseCase

    fun financeRepository(): FinanceRepository

    fun marketPriceRepository(): MarketPriceRepository

}

internal fun Context.watchlistWidgetEntryPoint(): WatchlistWidgetEntryPoint =
    EntryPointAccessors.fromApplication(
        applicationContext,
        WatchlistWidgetEntryPoint::class.java,
    )

/**
 * The very same assets the finance watchlist shows, in the order picked over there.
 *
 * Every asset is emitted only once its price has been fetched, so collecting this is what
 * refreshes the quotes in the first place.
 */
internal fun WatchlistWidgetEntryPoint.observeWatchlistAssets(): Flow<Async<List<WatchlistAsset>>> =
    observeWatchlistAssetsUseCase().invoke(
        sortTypeFlow = financeRepository().watchlistAssetsSortTypeFlow,
    )

