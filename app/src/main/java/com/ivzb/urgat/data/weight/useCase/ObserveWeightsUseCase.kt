package com.ivzb.urgat.data.weight.useCase

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseObserveUseCase
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.data.weight.WeightsRepository
import javax.inject.Inject

class ObserveWeightsUseCase @Inject constructor(
    weightsRepository: WeightsRepository
): BaseObserveUseCase<Weight, Unit>(weightsRepository) {

    override val errorMessage: Int = R.string.error_weights

    override fun filter(items: List<Weight>, filteringType: Unit): List<Weight> {
        return items
    }

}