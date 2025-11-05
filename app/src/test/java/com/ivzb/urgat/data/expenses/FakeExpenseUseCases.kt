package com.ivzb.urgat.data.expenses

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.useCase.CreateExpenseUseCase
import com.ivzb.urgat.data.expense.useCase.GetExpenseUseCase
import com.ivzb.urgat.data.expense.useCase.UpdateExpenseUseCase
import java.math.BigDecimal

class FakeGetExpenseUseCase : GetExpenseUseCase {
    var expenseToReturn: Expense? = null
    var calledWithId: String? = null

    override suspend fun invoke(expenseId: String): Expense? {
        calledWithId = expenseId
        return expenseToReturn
    }
}

class FakeCreateExpenseUseCase : CreateExpenseUseCase {
    var createdExpense: Triple<String, BigDecimal, String>? = null
    var date: Long? = null

    override suspend fun invoke(
        name: String,
        amount: BigDecimal,
        category: String,
        date: Long
    ) {
        createdExpense = Triple(name, amount, category)
        this.date = date
    }
}

class FakeUpdateExpenseUseCase : UpdateExpenseUseCase {
    var updatedExpense: Quadruple<String, String, BigDecimal, String>? = null
    var date: Long? = null

    override suspend fun invoke(
        id: String,
        name: String,
        amount: BigDecimal,
        category: String,
        date: Long
    ) {
        updatedExpense = Quadruple(id, name, amount, category)
        this.date = date
    }
}

// Helper data class for 4 values
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)