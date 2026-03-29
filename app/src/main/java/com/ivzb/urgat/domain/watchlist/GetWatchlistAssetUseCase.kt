package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import javax.inject.Inject

interface GetWatchlistAssetUseCase {

    suspend operator fun invoke(id: String): WatchlistAsset?

}

class DefaultGetWatchlistAssetUseCase @Inject constructor(
    private val repository: WatchlistAssetsRepository
): GetWatchlistAssetUseCase {

    override suspend fun invoke(id: String): WatchlistAsset? {
        return repository.get(id)
    }

}

