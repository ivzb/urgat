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

fun InvestmentEntity.toExternal() = Investment(
    id = id,
    name = name,
    shares = shares,
    costPrice = cost,
    marketPrice = BigDecimal.ZERO,
    category = category,
    currency = currency,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<InvestmentEntity>.toExternal() = map(InvestmentEntity::toExternal)