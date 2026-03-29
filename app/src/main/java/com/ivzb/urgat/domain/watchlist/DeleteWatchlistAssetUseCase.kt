package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import javax.inject.Inject

interface DeleteWatchlistAssetUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultDeleteWatchlistAssetUseCase @Inject constructor(
    private val repository: WatchlistAssetsRepository
): DeleteWatchlistAssetUseCase {

    override suspend operator fun invoke(id: String) {
        repository.delete(id)
    }

}

