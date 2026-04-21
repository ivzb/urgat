package com.ivzb.urgat.data.trading

fun Trading.toEntity() = TradingEntity(
    id = id,
    name = name,
    shares = shares,
    marketPrice = marketPrice,
    fee = fee,
    currency = currency,
    orderType = orderType,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Trading>.toEntity() = map(Trading::toEntity)

fun TradingEntity.toExternal(): Trading {
    return Trading(
        id = id,
        name = name,
        shares = shares,
        marketPrice = marketPrice,
        fee = fee,
        currency = currency,
        orderType = orderType,
        date = date,
        isDeleted = isDeleted,
        createdAt = createdAt
    )
}

fun List<TradingEntity>.toExternal() = map(TradingEntity::toExternal)

