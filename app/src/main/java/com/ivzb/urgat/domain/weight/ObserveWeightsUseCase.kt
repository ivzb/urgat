package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.data.weight.WeightsRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.weights.WeightsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveWeightsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<Unit>? = null,
        sortTypeFlow: Flow<WeightsSortType>? = null,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<Weight>>>

}

class DefaultObserveWeightsUseCase @Inject constructor(
    repository: WeightsRepository
): ObserveWeightsUseCase {

    private val observeUseCase = ObserveUseCase<Weight, Unit, WeightsSortType>(
        repository = repository,
        sort = ::sortWeights,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<WeightsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<Weight>>> = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}