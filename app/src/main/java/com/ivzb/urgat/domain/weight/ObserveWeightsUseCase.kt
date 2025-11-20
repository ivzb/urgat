package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.base.BaseObserveUseCase
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.data.weight.WeightsRepository
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveWeightsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<Unit>?,
        limit: Int
    ): Flow<Async<List<Weight>>>

}

class DefaultObserveWeightsUseCase @Inject constructor(
    repository: WeightsRepository
): ObserveWeightsUseCase, BaseObserveUseCase<Weight, Unit>(repository) {

    override val errorMessage: Int = R.string.error_weights

    override fun transform(items: List<Weight>): List<Weight> =
        items
            .groupBy { it.createdAt / MILLISECONDS_PER_DAY }
            .map { (_, items) -> items.last() }
            .sortedBy { it.createdAt }

    companion object {
        private const val MILLISECONDS_PER_DAY = 86_400_000L
    }

}