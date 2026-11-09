package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.R
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortMenuItem
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

interface ObserveWatchlistAssetsSortingUseCase {

    operator fun invoke(): Flow<Async<List<WatchlistAssetsSortMenuItem>>>

}

class DefaultObserveWatchlistAssetsSortingUseCase @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ObserveWatchlistAssetsSortingUseCase {

    override fun invoke(): Flow<Async<List<WatchlistAssetsSortMenuItem>>> = flowOf(
        Async.Success(
            listOf(
                SortMenuItem(type = WatchlistAssetsSortType.BY_DATE, labelResId = R.string.sort_by_date),
                SortMenuItem(type = WatchlistAssetsSortType.BY_NAME, labelResId = R.string.sort_by_name),
            )
        )
    ).flowOn(dispatcher)

}
