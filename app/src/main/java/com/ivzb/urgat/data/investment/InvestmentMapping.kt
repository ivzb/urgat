package com.ivzb.urgat.data.investment

import java.math.BigDecimal

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

fun InvestmentEntity.toExternal(
    description: String?,
    marketPrice: BigDecimal?,
    pricePrecision: Int?,
): Investment {
    return Investment(
        id = id,
        name = name,
        description = description,
        shares = shares,
        costPrice = cost,
        marketPrice = marketPrice ?: cost,
        pricePrecision = pricePrecision,
        category = category,
        currency = currency,
        date = date,
        isDeleted = isDeleted,
        createdAt = createdAt
    )
}