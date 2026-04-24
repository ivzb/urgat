package com.ivzb.urgat.data.trade

fun Trade.toEntity() = TradeEntity(
    id = id,
    name = name,
    shares = shares,
    fee = fee,
    marketPrice = marketPrice,
    currency = currency,
    orderType = orderType,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Trade>.toEntity() = map(Trade::toEntity)

fun TradeEntity.toExternal(
    pricePrecision: Int?,
): Trade {
    return Trade(
        id = id,
        name = name,
        shares = shares,
        fee = fee,
        marketPrice = marketPrice,
        currency = currency,
        orderType = orderType,
        pricePrecision = pricePrecision,
        date = date,
        isDeleted = isDeleted,
        createdAt = createdAt
    )
}