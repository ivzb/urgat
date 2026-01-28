package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.ui.savings.SavingsSortMenuItem
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class FakeObserveSavingsUseCase : ObserveSavingsUseCase {

    private val flow = MutableStateFlow<Async<List<Saving>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<Saving>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Saving>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedSavingsMonthlyUseCase : ObserveGroupedSavingsMonthlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedSavings>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedSavings>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedSavings>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedSavingCategoriesMonthlyUseCase: ObserveGroupedSavingCategoriesMonthlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedSavingCategories>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedSavingCategories>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedSavingCategories>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedSavingCategoriesYearlyUseCase: ObserveGroupedSavingCategoriesYearlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedSavingCategories>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedSavingCategories>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedSavingCategories>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedSavingCategoriesAllTimeUseCase: ObserveGroupedSavingCategoriesAllTimeUseCase {

    private val flow = MutableStateFlow<Async<List<SavingCategory>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<SavingCategory>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<SavingCategory>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeGetSavingUseCase : GetSavingUseCase {

    var toReturn: Saving? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): Saving? {
        calledWithId = id
        return toReturn
    }
}

class FakeCreateSavingUseCase : CreateSavingUseCase {

    var created: List<Any> = listOf()

    override suspend fun invoke(
        name: String,
        amount: BigDecimal,
        date: Long,
    ) {
        created = listOf(name, amount, date)
    }
}

class FakeUpdateSavingUseCase : UpdateSavingUseCase {

    var updated: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
        name: String,
        amount: BigDecimal,
        date: Long,
    ) {
        updated = listOf(id, name, amount, date)
    }
}

class FakeObserveSavingsSortingUseCase : ObserveSavingsSortingUseCase {

    private val flow = MutableStateFlow<Async<List<SavingsSortMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<SavingsSortMenuItem>>) {
        flow.value = value
    }

    override operator fun invoke(): Flow<Async<List<SavingsSortMenuItem>>> {
        return flow
    }

}

class FakeDeleteSavingUseCase : DeleteSavingUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
    ) {
        deleted = listOf(id)
    }
}
