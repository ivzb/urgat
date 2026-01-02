package com.ivzb.urgat.domain.weight

import com.ivzb.urgat.data.weight.WeightsRepository
import javax.inject.Inject

interface DeleteWeightUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultDeleteWeightUseCase @Inject constructor(
    private val repository: WeightsRepository
): DeleteWeightUseCase {

    override suspend operator fun invoke(id: String) {
        repository.delete(id)
    }

}