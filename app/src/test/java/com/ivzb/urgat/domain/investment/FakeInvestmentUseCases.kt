package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.ui.investments.InvestmentsFilterType
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
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<InvestmentsFilterType>?,
        limit: Int
    ): Flow<Async<List<Investment>>> {
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
        currency: String,
        amount: BigDecimal,
        category: String
    ) {
        created = listOf(name, currency, amount, category)
    }
}

class FakeUpdateInvestmentUseCase : UpdateInvestmentUseCase {

    var updated: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
        name: String,
        currency: String,
        amount: BigDecimal,
        category: String,
    ) {
        updated = listOf(id, name, currency, amount, category)
    }
}