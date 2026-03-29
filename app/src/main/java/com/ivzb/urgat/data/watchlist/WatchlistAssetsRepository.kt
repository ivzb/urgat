package com.ivzb.urgat.data.watchlist

import com.ivzb.urgat.data.base.BaseRepository

interface WatchlistAssetsRepository: BaseRepository<WatchlistAsset> {

    suspend fun create(
        name: String,
    ): String

    suspend fun update(
        id: String,
        name: String,
    )

}