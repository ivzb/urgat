package com.ivzb.urgat.domain.budgets

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.domain.budget.ObserveBudgetsUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FakeObserveBudgetsUseCase : ObserveBudgetsUseCase {

    private val flow = MutableStateFlow<Async<List<Budget>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<Budget>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>?,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Budget>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}
