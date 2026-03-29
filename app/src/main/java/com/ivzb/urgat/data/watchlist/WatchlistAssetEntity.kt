package com.ivzb.urgat.data.watchlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_assets")
data class WatchlistAssetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isDeleted: Boolean,
    val createdAt: Long
)