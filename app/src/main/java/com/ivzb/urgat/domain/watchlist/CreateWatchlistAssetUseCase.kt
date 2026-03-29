package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import javax.inject.Inject

interface CreateWatchlistAssetUseCase {

    suspend operator fun invoke(
        name: String,
    )

}

class DefaultCreateWatchlistAssetUseCase @Inject constructor(
    private val repository: WatchlistAssetsRepository
): CreateWatchlistAssetUseCase {

    override suspend operator fun invoke(
        name: String,
    ) {
        repository.create(name)
    }

}