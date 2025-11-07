package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.base.BaseObserveUseCase
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.data.weight.WeightsRepository
import javax.inject.Inject

class ObserveWeightsUseCase @Inject constructor(
    weightsRepository: WeightsRepository
): BaseObserveUseCase<Weight, Unit>(weightsRepository) {

    override val errorMessage: Int = R.string.error_weights

    override fun transform(items: List<Weight>): List<Weight> =
        items
            .groupBy { it.createdAt / MILLIES_PER_DAY }
            .map { (_, items) -> items.last() }
            .sortedBy { it.createdAt }

    companion object {
        private const val MILLIES_PER_DAY = 86_400_000L
    }

}