package com.ivzb.urgat.data.watchlist

import com.ivzb.urgat.data.marketPrice.MarketPrice

fun WatchlistAsset.toEntity() = WatchlistAssetEntity(
    id = id,
    name = name,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<WatchlistAsset>.toEntity() = map(WatchlistAsset::toEntity)

fun WatchlistAssetEntity.toExternal(marketPrice: MarketPrice?): WatchlistAsset {
    return WatchlistAsset(
        id = id,
        name = name,
        description = marketPrice?.description,
        marketPrice = marketPrice?.price,
        currency = marketPrice?.currency,
        previousClosePrice = marketPrice?.previousClosePrice,
        pricePrecision = marketPrice?.pricePrecision,
        isDeleted = isDeleted,
        createdAt = createdAt
    )
}