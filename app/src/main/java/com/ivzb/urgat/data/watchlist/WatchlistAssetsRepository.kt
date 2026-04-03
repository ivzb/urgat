package com.ivzb.urgat.data.watchlist

import com.ivzb.urgat.data.base.BaseRepository
import kotlinx.coroutines.flow.StateFlow

interface WatchlistAssetsRepository: BaseRepository<WatchlistAsset> {

    val isRefreshingFlow: StateFlow<Boolean>

    suspend fun create(
        name: String,
    ): String

    suspend fun update(
        id: String,
        name: String,
    )

}