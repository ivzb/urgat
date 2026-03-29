package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAsset

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

