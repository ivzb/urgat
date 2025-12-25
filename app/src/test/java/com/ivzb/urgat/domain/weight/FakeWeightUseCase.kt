package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class FakeObserveWeightsUseCase : ObserveWeightsUseCase {

    private val flow = MutableStateFlow<Async<List<Weight>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<Weight>>) {
        flow.value = value
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>?,
        filterTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Weight>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeCreateWeightUseCase : CreateWeightUseCase {

    var created: List<Any> = listOf()

    override suspend fun invoke(
        value: BigDecimal,
        date: Long,
    ) {
        created = listOf(value, date)
    }
}
