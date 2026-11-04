package com.ivzb.urgat.data.trade

fun Trade.toEntity() = TradeEntity(
    id = id,
    name = name,
    amount = amount,
    currency = currency,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Trade>.toEntity() = map(Trade::toEntity)

fun TradeEntity.toExternal(): Trade = Trade(
    id = id,
    name = name,
    amount = amount,
    currency = currency,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)
