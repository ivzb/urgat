package com.ivzb.urgat.domain.watchlist

import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import javax.inject.Inject

interface UpdateWatchlistAssetUseCase {

    suspend operator fun invoke(
        id: String,
        name: String,
    )

}

class DefaultUpdateWatchlistAssetUseCase @Inject constructor(
    private val repository: WatchlistAssetsRepository
): UpdateWatchlistAssetUseCase {

    override suspend operator fun invoke(
        id: String,
        name: String,
    ) {
        repository.update(id, name)
    }

}