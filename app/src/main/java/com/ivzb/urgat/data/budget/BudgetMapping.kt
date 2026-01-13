package com.ivzb.urgat.data.budget

fun Budget.toEntity() = BudgetEntity(
    id = id,
    income = income,
    bankStart = bankStart,
    bankEnd = bankEnd,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Budget>.toEntity() = map(Budget::toEntity)

fun BudgetEntity.toExternal() = Budget(
    id = id,
    income = income,
    bankStart = bankStart,
    bankEnd = bankEnd,
    date = date,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<BudgetEntity>.toExternal() = map(BudgetEntity::toExternal)