package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.SavingsRepository
import javax.inject.Inject

interface DeleteSavingUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultDeleteSavingUseCase @Inject constructor(
    private val repository: SavingsRepository
): DeleteSavingUseCase {

    override suspend operator fun invoke(id: String) {
        repository.delete(id)
    }

}