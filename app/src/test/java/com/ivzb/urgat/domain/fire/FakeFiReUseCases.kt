package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class FakeObserveFiReUseCase : ObserveFiReUseCase {

    private val flow = MutableStateFlow<Async<FiRe?>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<FiRe?>) {
        flow.value = value
    }

    override operator fun invoke(
        withdrawalRateFlow: Flow<BigDecimal>,
        returnOfInvestmentsFlow: Flow<BigDecimal>,
        taxRateFlow: Flow<BigDecimal>,

        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<FiRe?>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}