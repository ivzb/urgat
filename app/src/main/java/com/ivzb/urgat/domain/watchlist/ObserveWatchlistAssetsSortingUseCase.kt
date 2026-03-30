package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortMenuItem
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface ObserveWatchlistAssetsSortingUseCase {

    operator fun invoke(): Flow<Async<List<WatchlistAssetsSortMenuItem>>>

}

class DefaultObserveWatchlistAssetsSortingUseCase @Inject constructor() : ObserveWatchlistAssetsSortingUseCase {

    override fun invoke(): Flow<Async<List<WatchlistAssetsSortMenuItem>>> {
        return flowOf(Async.Success(listOf(
            SortMenuItem(type = WatchlistAssetsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = WatchlistAssetsSortType.BY_NAME, labelResId = R.string.sort_by_name),
        )))
    }

}

