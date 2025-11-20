package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.WeightsRepository
import java.math.BigDecimal
import javax.inject.Inject

interface CreateWeightUseCase {

    suspend operator fun invoke(value: BigDecimal)

}

class DefaultCreateWeightUseCase @Inject constructor(
    private val repository: WeightsRepository
): CreateWeightUseCase {

    override suspend operator fun invoke(value: BigDecimal) {
        repository.create(value)
    }

}
