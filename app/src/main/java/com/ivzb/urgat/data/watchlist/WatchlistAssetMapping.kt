package com.ivzb.urgat.data.watchlist

import com.ivzb.urgat.data.marketPrice.MarketPrice
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

typealias FetchMarketPrice = suspend (symbol: String) -> MarketPrice?

fun WatchlistAsset.toEntity() = WatchlistAssetEntity(
    id = id,
    name = name,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<WatchlistAsset>.toEntity() = map(WatchlistAsset::toEntity)

suspend fun WatchlistAssetEntity.toExternal(fetchMarketPrice: FetchMarketPrice): WatchlistAsset {
    val marketPrice = fetchMarketPrice(name)

    return WatchlistAsset(
        id = id,
        name = name,
        description = marketPrice?.description,
        marketPrice = marketPrice?.price,
        currency = marketPrice?.currency,
        previousClosePrice = marketPrice?.previousClosePrice,
        isDeleted = isDeleted,
        createdAt = createdAt
    )
}

@JvmName("entityToExternal")
suspend fun List<WatchlistAssetEntity>.toExternal(fetchMarketPrice: FetchMarketPrice) =
    coroutineScope {
        map { async { it.toExternal(fetchMarketPrice) } }.awaitAll()
    }