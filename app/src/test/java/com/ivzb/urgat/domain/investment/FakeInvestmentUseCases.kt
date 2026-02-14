package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.ui.investments.InvestmentsFilterMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsSortMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class FakeObserveInvestmentsUseCase : ObserveInvestmentsUseCase {

    private val flow = MutableStateFlow<Async<List<Investment>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<Investment>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Investment>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedInvestmentsMonthlyUseCase : ObserveGroupedInvestmentsMonthlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedInvestments>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedInvestments>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedInvestments>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveInvestmentNetWorthUseCase : ObserveInvestmentNetWorthUseCase {

    private val flow = MutableStateFlow<Async<InvestmentNetWorth>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<InvestmentNetWorth>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<InvestmentNetWorth>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveInvestmentCategoriesUseCase : ObserveInvestmentCategoriesUseCase {

    private val flow = MutableStateFlow<Async<List<InvestmentCategory>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<InvestmentCategory>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<InvestmentCategory>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveInvestmentAssetsUseCase : ObserveInvestmentAssetsUseCase {

    private val flow = MutableStateFlow<Async<List<InvestmentAsset>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<InvestmentAsset>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<InvestmentAsset>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedInvestmentCategoriesYearlyUseCase: ObserveGroupedInvestmentCategoriesYearlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedInvestmentCategories>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedInvestmentCategories>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedInvestmentCategories>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeGetInvestmentUseCase : GetInvestmentUseCase {

    var toReturn: Investment? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): Investment? {
        calledWithId = id
        return toReturn
    }
}

class FakeCreateInvestmentUseCase : CreateInvestmentUseCase {

    var created: List<Any> = listOf()

    override suspend fun invoke(
        name: String,
        shares: BigDecimal,
        cost: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    ) {
        created = listOf(name, shares, cost, category, currency, date)
    }
}

class FakeUpdateInvestmentUseCase : UpdateInvestmentUseCase {

    var updated: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
        name: String,
        shares: BigDecimal,
        cost: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    ) {
        updated = listOf(id, name, shares, cost, category, currency, date)
    }
}

class FakeObserveInvestmentsFilteringUseCase : ObserveInvestmentsFilteringUseCase {

    private val flow = MutableStateFlow<Async<List<InvestmentsFilterMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<InvestmentsFilterMenuItem>>) {
        flow.value = value
    }

    override operator fun invoke(): Flow<Async<List<InvestmentsFilterMenuItem>>> {
        return flow
    }

}

class FakeObserveInvestmentsSortingUseCase : ObserveInvestmentsSortingUseCase {

    private val flow = MutableStateFlow<Async<List<InvestmentsSortMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<InvestmentsSortMenuItem>>) {
        flow.value = value
    }

    override operator fun invoke(): Flow<Async<List<InvestmentsSortMenuItem>>> {
        return flow
    }

}

class FakeDeleteInvestmentUseCase : DeleteInvestmentUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
    ) {
        deleted = listOf(id)
    }
}

class FakeObserveInvestmentsUiStateUseCase: ObserveInvestmentsUiStateUseCase {

    private val flow = MutableStateFlow<Async<InvestmentsUiState>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<InvestmentsUiState>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>,
        sortTypeFlow: Flow<InvestmentsSortType>
    ): Flow<Async<InvestmentsUiState>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}
