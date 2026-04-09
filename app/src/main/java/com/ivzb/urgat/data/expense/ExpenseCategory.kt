package com.ivzb.urgat.data.expense

data class ExpenseCategory(
    val id: String,
    val name: String,
    val totals: List<ExpenseTotal>,

    val expenses: List<Expense>,
)