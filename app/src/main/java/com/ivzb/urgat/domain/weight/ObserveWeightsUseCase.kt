package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.data.weight.WeightsRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveWeightsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        filterTypeFlow: Flow<Unit>? = null,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<Weight>>>

}

class DefaultObserveWeightsUseCase @Inject constructor(
    repository: WeightsRepository
): ObserveWeightsUseCase {

    private val observeUseCase = ObserveUseCase<Weight, Unit>(
        repository = repository,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        filterTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<Weight>>> = observeUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

}