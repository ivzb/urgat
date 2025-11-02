package com.ivzb.urgat.data.weight.useCase

import com.ivzb.urgat.data.weight.WeightsRepository
import java.math.BigDecimal
import javax.inject.Inject

class CreateWeightUseCase @Inject constructor(
    private val weightsRepository: WeightsRepository
) {

    suspend operator fun invoke(value: BigDecimal) {
        weightsRepository.create(value)
    }

}
