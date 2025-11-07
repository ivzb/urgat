package com.ivzb.urgat.domain.expenses

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.expense.GetExpenseUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesUseCase
import com.ivzb.urgat.domain.expense.UpdateExpenseUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class FakeObserveExpensesUseCase : ObserveExpensesUseCase {

    private val flow = MutableStateFlow<Async<List<Expense>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<Expense>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limit: Int
    ): Flow<Async<List<Expense>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

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