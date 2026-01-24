package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingsRepository
import javax.inject.Inject

interface GetSavingUseCase {

    suspend operator fun invoke(id: String): Saving?

}

class DefaultGetSavingUseCase @Inject constructor(
    private val repository: SavingsRepository
): GetSavingUseCase {

    override suspend fun invoke(id: String): Saving? {
        return repository.get(id)
    }

}