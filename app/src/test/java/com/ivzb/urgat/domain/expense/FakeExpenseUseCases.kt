package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.ui.expenses.ExpensesFilterMenuItem
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
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Expense>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedExpensesUseCase : ObserveGroupedExpensesUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedItems<String, Expense>>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedItems<String, Expense>>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedItems<String, Expense>>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeGetExpenseUseCase : GetExpenseUseCase {

    var toReturn: Expense? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): Expense? {
        calledWithId = id
        return toReturn
    }
}

class FakeCreateExpenseUseCase : CreateExpenseUseCase {

    var created: List<Any> = listOf()

    override suspend fun invoke(
        name: String,
        amount: BigDecimal,
        category: String,
        date: Long
    ) {
        created = listOf(name, amount, category)
    }
}

class FakeUpdateExpenseUseCase : UpdateExpenseUseCase {

    var updated: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
        name: String,
        amount: BigDecimal,
        category: String,
        date: Long
    ) {
        updated = listOf(id, name, amount, category)
    }
}

class FakeObserveExpensesFilteringUseCase : ObserveExpensesFilteringUseCase {

    private val flow = MutableStateFlow<Async<List<ExpensesFilterMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<ExpensesFilterMenuItem>>) {
        flow.value = value
    }

    override operator fun invoke(): Flow<Async<List<ExpensesFilterMenuItem>>> {
        return flow
    }

}

class FakeDeleteExpenseUseCase : DeleteExpenseUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
    ) {
        deleted = listOf(id)
    }
}
