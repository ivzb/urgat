package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.ui.weights.WeightsSortMenuItem
import com.ivzb.urgat.ui.weights.WeightsSortType
import com.ivzb.urgat.ui.weights.WeightsUiState
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
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<WeightsSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Weight>>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveWeightsSortingUseCase : ObserveWeightsSortingUseCase {

    private val flow = MutableStateFlow<Async<List<WeightsSortMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<WeightsSortMenuItem>>) {
        flow.value = value
    }

    override operator fun invoke(): Flow<Async<List<WeightsSortMenuItem>>> {
        return flow
    }

}

class FakeGetWeightUseCase : GetWeightUseCase {

    var toReturn: Weight? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): Weight? {
        calledWithId = id
        return toReturn
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

class FakeUpdateWeightUseCase : UpdateWeightUseCase {

    var updated: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
        value: BigDecimal,
        date: Long,
    ) {
        updated = listOf(id, value, date)
    }
}

class FakeDeleteWeightUseCase : DeleteWeightUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
    ) {
        deleted = listOf(id)
    }
}

class FakeObserveWeightsUiStateUseCase : ObserveWeightsUiStateUseCase {

    private val flow = MutableStateFlow<Async<WeightsUiState>>(Async.Loading)

    fun emit(value: Async<WeightsUiState>) {
        flow.value = value
    }

    override fun invoke(
        sortTypeFlow: Flow<WeightsSortType>,
        limitFlow: Flow<Int>?,
    ): Flow<Async<WeightsUiState>> {
        return flow
    }

}

