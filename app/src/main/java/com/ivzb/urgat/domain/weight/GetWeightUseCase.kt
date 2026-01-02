package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.data.weight.WeightsRepository
import javax.inject.Inject

interface GetWeightUseCase {

    suspend operator fun invoke(id: String): Weight?

}

class DefaultGetWeightUseCase @Inject constructor(
    private val repository: WeightsRepository
): GetWeightUseCase {

    override suspend fun invoke(id: String): Weight? {
        return repository.get(id)
    }

}