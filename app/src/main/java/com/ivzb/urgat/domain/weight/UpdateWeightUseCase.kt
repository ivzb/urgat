package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.WeightsRepository
import java.math.BigDecimal
import javax.inject.Inject

interface UpdateWeightUseCase {

    suspend operator fun invoke(
        id: String,
        value: BigDecimal,
        date: Long,
    )

}

class DefaultUpdateWeightUseCase @Inject constructor(
    private val repository: WeightsRepository
): UpdateWeightUseCase {

    override suspend operator fun invoke(
        id: String,
        value: BigDecimal,
        date: Long,
    ) {

        repository.update(id, value, date)
    }

}
