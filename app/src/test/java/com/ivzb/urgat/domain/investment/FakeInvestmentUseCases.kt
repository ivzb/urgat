package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.ui.investments.InvestmentsFilterMenuItem
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
        filterTypeFlow: Flow<InvestmentsFilterMenuItem?>?,
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
        amount: BigDecimal,
        cost: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    ) {
        created = listOf(name, amount, cost, category, currency, date)
    }
}

class FakeUpdateInvestmentUseCase : UpdateInvestmentUseCase {

    var updated: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
        name: String,
        amount: BigDecimal,
        cost: BigDecimal,
        category: String,
        currency: String,
        date: Long,
    ) {
        updated = listOf(id, name, amount, cost, category, currency, date)
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

class FakeDeleteInvestmentUseCase : DeleteInvestmentUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
    ) {
        deleted = listOf(id)
    }
}
