package com.ivzb.urgat.data.expense

fun Expense.toEntity() = ExpenseEntity(
    id = id,
    name = name,
    amount = amount,
    category = category,
    date = date,
    excludeFromAnalytics = excludeFromAnalytics,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Expense>.toEntity() = map(Expense::toEntity)

fun ExpenseEntity.toExternal() = Expense(
    id = id,
    name = name,
    amount = amount,
    category = category,
    date = date,
    excludeFromAnalytics = excludeFromAnalytics,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<ExpenseEntity>.toExternal() = map(ExpenseEntity::toExternal)