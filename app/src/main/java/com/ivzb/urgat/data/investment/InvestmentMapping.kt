package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.investmentMarketPrice.CalculateMarketPrice
import com.ivzb.urgat.data.marketPrice.FetchMarketPrice
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

fun Investment.toEntity() = InvestmentEntity(
    id = id,
    name = name,
    shares = shares,
    cost = costPrice,
    category = category,
    currency = currency,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Investment>.toEntity() = map(Investment::toEntity)

suspend fun InvestmentEntity.toExternal(
    fetchMarketPrice: FetchMarketPrice,
    calculateMarketPrice: CalculateMarketPrice,
): Investment {
    val marketPrice = fetchMarketPrice(name)

    return Investment(
        id = id,
        name = name,
        description = marketPrice?.description,
        shares = shares,
        costPrice = cost,
        marketPrice = calculateMarketPrice(name, currency) ?: cost,
        category = category,
        currency = currency,
        date = date,
        isDeleted = isDeleted,
        createdAt = createdAt
    )
}

@JvmName("entityToExternal")
suspend fun List<InvestmentEntity>.toExternal(
    fetchMarketPrice: FetchMarketPrice,
    calculateMarketPrice: CalculateMarketPrice,
) =
    coroutineScope {
        map { async { it.toExternal(fetchMarketPrice, calculateMarketPrice) } }.awaitAll()
    }