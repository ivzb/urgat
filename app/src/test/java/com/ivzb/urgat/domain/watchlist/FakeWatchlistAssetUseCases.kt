package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortMenuItem
import com.ivzb.urgat.ui.watchlist.WatchlistAssetsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FakeGetWatchlistAssetUseCase : GetWatchlistAssetUseCase {

    var toReturn: WatchlistAsset? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): WatchlistAsset? {
        calledWithId = id
        return toReturn
    }
}

class FakeCreateWatchlistAssetUseCase : CreateWatchlistAssetUseCase {

    var created: List<Any> = listOf()

    override suspend fun invoke(
        name: String,
    ) {
        created = listOf(name)
    }
}

class FakeUpdateWatchlistAssetUseCase : UpdateWatchlistAssetUseCase {

    var updated: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
        name: String,
    ) {
        updated = listOf(id, name)
    }
}

class FakeDeleteWatchlistAssetUseCase : DeleteWatchlistAssetUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
    ) {
        deleted = listOf(id)
    }
}

class FakeObserveWatchlistAssetsUseCase : ObserveWatchlistAssetsUseCase {

    private val flow = MutableStateFlow<Async<List<WatchlistAsset>>>(Async.Loading)

    private val _isRefreshingFlow = MutableStateFlow(false)
    override val isRefreshingFlow: StateFlow<Boolean> = _isRefreshingFlow

    var lastQuery: String = ""

    fun emit(value: Async<List<WatchlistAsset>>) {
        flow.value = value
    }

    fun emitRefreshing(isRefreshing: Boolean) {
        _isRefreshingFlow.value = isRefreshing
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<WatchlistAssetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<WatchlistAsset>>> {
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveWatchlistAssetsSortingUseCase : ObserveWatchlistAssetsSortingUseCase {

    private val flow = MutableStateFlow<Async<List<WatchlistAssetsSortMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<WatchlistAssetsSortMenuItem>>) {
        flow.value = value
    }

    override operator fun invoke(): Flow<Async<List<WatchlistAssetsSortMenuItem>>> {
        return flow
    }

}
