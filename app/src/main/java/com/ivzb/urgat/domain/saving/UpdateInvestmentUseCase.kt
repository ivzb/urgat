package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.SavingsRepository
import java.math.BigDecimal
import javax.inject.Inject

interface UpdateSavingUseCase {

    suspend operator fun invoke(
        id: String,
        name: String,
        amount: BigDecimal,
        date: Long,
    )

}

class DefaultUpdateSavingUseCase @Inject constructor(
    private val repository: SavingsRepository
): UpdateSavingUseCase {

    override suspend operator fun invoke(
        id: String,
        name: String,
        amount: BigDecimal,
        date: Long,
    ) {

        repository.update(id, name, amount, date)
    }

}
