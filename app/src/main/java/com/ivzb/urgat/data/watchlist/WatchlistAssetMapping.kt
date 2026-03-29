package com.ivzb.urgat.data.watchlist

fun WatchlistAsset.toEntity() = WatchlistAssetEntity(
    id = id,
    name = name,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<WatchlistAsset>.toEntity() = map(WatchlistAsset::toEntity)

fun WatchlistAssetEntity.toExternal() = WatchlistAsset(
    id = id,
    name = name,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<WatchlistAssetEntity>.toExternal() = map(WatchlistAssetEntity::toExternal)