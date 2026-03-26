package com.ivzb.urgat.data.investment

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.math.BigDecimal

typealias CalculateMarketPrice = suspend (symbol: String, currency: String) -> BigDecimal

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

suspend fun InvestmentEntity.toExternal(calculateMarketPrice: CalculateMarketPrice) = Investment(
    id = id,
    name = name,
    shares = shares,
    costPrice = cost,
    marketPrice = calculateMarketPrice(name, currency),
    category = category,
    currency = currency,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
suspend fun List<InvestmentEntity>.toExternal(calculateMarketPrice: CalculateMarketPrice) =
    coroutineScope {
        map { async { it.toExternal(calculateMarketPrice) } }.awaitAll()
    }