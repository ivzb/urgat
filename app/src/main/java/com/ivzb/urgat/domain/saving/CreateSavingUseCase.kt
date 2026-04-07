package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.SavingsRepository
import java.math.BigDecimal
import javax.inject.Inject

interface CreateSavingUseCase {

    suspend operator fun invoke(
        name: String,
        amount: BigDecimal,
        currency: String,
        date: Long,
    )

}

class DefaultCreateSavingUseCase @Inject constructor(
    private val repository: SavingsRepository
): CreateSavingUseCase {

    override suspend operator fun invoke(
        name: String,
        amount: BigDecimal,
        currency: String,
        date: Long,
    ) {

        repository.create(name, amount, currency, date)
    }

}
