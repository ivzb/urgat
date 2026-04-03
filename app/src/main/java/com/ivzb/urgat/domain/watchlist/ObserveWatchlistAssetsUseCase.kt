package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface ObserveWatchlistAssetsUseCase {

    val isRefreshingFlow: StateFlow<Boolean>

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<Unit>? = null,
        sortTypeFlow: Flow<WatchlistAssetsSortType>?,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<WatchlistAsset>>>

}

class DefaultObserveWatchlistAssetsUseCase @Inject constructor(
    repository: WatchlistAssetsRepository
): ObserveWatchlistAssetsUseCase {

    override val isRefreshingFlow: StateFlow<Boolean> = repository.isRefreshingFlow

    private val observeUseCase = ObserveUseCase(
        repository = repository,
        filter = ::filterWatchlistAssets,
        sort = ::sortWatchlistAssets,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<WatchlistAssetsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<WatchlistAsset>>> = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}

