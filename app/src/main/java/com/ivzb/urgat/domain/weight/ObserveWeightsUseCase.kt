package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.data.weight.WeightsRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveWeightsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Weight>>>

}

class DefaultObserveWeightsUseCase @Inject constructor(
    repository: WeightsRepository
): ObserveWeightsUseCase {

    private val observeUseCase = ObserveUseCase<Weight, Unit>(
        repository = repository,
        transform = ::transform,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Weight>>> = observeUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

    companion object {

        private fun transform(items: List<Weight>): List<Weight> =
            items
                .groupBy { it.date / MILLISECONDS_PER_DAY }
                .map { (_, items) -> items.first() }
                .sortedBy { it.date }


        private const val MILLISECONDS_PER_DAY = 86_400_000L
    }

}