package com.ivzb.urgat.data.investment

fun Investment.toEntity() = InvestmentEntity(
    id = id,
    name = name,
    shares = shares,
    cost = cost,
    category = category,
    currency = currency,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Investment>.toEntity() = map(Investment::toEntity)

fun InvestmentEntity.toExternal() = Investment(
    id = id,
    name = name,
    shares = shares,
    cost = cost,
    category = category,
    currency = currency,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<InvestmentEntity>.toExternal() = map(InvestmentEntity::toExternal)