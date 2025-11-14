package com.ivzb.urgat.data.investment

fun Investment.toEntity() = InvestmentEntity(
    id = id,
    name = name,
    currency = currency,
    amount = amount,
    category = category,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Investment>.toEntity() = map(Investment::toEntity)

fun InvestmentEntity.toExternal() = Investment(
    id = id,
    name = name,
    currency = currency,
    amount = amount,
    category = category,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<InvestmentEntity>.toExternal() = map(InvestmentEntity::toExternal)