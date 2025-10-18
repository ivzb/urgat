package com.ivzb.urgat.data.expense

fun Expense.toEntity() = ExpenseEntity(
    id = id,
    name = name,
    amount = amount,
    isDeleted = isDeleted,
    createdAt = createdAt
)

fun List<Expense>.toEntity() = map(Expense::toEntity)

fun ExpenseEntity.toExternal() = Expense(
    id = id,
    name = name,
    amount = amount,
    isDeleted = isDeleted,
    createdAt = createdAt
)

@JvmName("entityToExternal")
fun List<ExpenseEntity>.toExternal() = map(ExpenseEntity::toExternal)