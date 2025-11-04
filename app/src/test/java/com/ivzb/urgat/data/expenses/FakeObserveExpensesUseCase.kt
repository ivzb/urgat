package com.ivzb.urgat.data.expenses

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.useCase.ObserveExpensesUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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
