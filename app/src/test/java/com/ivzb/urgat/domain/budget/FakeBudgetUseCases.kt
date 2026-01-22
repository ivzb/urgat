package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.ui.budgets.BudgetsFilterMenuItem
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortMenuItem
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

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

class FakeGetBudgetUseCase : GetBudgetUseCase {

    var toReturn: Budget? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): Budget? {
        calledWithId = id
        return toReturn
    }
}

class FakeCreateBudgetUseCase : CreateBudgetUseCase {

    var created: List<Any?> = listOf()

    override suspend fun invoke(
        income: BigDecimal,
        bankStart: BigDecimal,
        bankEnd: BigDecimal?,
        date: Long,
    ) {
        created = listOf(income, bankStart, bankEnd, date)
    }
}

class FakeUpdateBudgetUseCase : UpdateBudgetUseCase {

    var updated: List<Any?> = listOf()

    override suspend fun invoke(
        id: String,
        income: BigDecimal,
        bankStart: BigDecimal,
        bankEnd: BigDecimal?,
        date: Long,
    ) {
        updated = listOf(id, income, bankStart, bankEnd, date)
    }
}

class FakeDeleteBudgetUseCase : DeleteBudgetUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
    ) {
        deleted = listOf(id)
    }
}

class FakeObserveBudgetsFilteringUseCase : ObserveBudgetsFilteringUseCase {

    private val flow = MutableStateFlow<Async<List<BudgetsFilterMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<BudgetsFilterMenuItem>>) {
        flow.value = value
    }

    override operator fun invoke(): Flow<Async<List<BudgetsFilterMenuItem>>> {
        return flow
    }

}

class FakeObserveBudgetsSortingUseCase : ObserveBudgetsSortingUseCase {

    private val flow = MutableStateFlow<Async<List<BudgetsSortMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<BudgetsSortMenuItem>>) {
        flow.value = value
    }

    override operator fun invoke(): Flow<Async<List<BudgetsSortMenuItem>>> {
        return flow
    }

}

class FakeObserveGroupedBudgetsMonthlyUseCase: ObserveGroupedBudgetsMonthlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedBudget>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedBudget>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedBudget>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedBudgetCategoriesMonthlyUseCase: ObserveGroupedBudgetCategoriesMonthlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedBudgetCategories>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedBudgetCategories>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedBudgetCategories>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedBudgetsYearlyUseCase: ObserveGroupedBudgetsYearlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedBudget>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedBudget>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedBudget>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedBudgetsAllTimeUseCase: ObserveGroupedBudgetsAllTimeUseCase {

    private val flow = MutableStateFlow<Async<GroupedBudget>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<GroupedBudget>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<BudgetsFilterType>?,
        sortTypeFlow: Flow<BudgetsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<GroupedBudget>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}
