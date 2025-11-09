package com.ivzb.urgat.domain.weight

import java.math.BigDecimal

class FakeCreateWeightUseCase : CreateWeightUseCase {

    var created: BigDecimal? = null

    override suspend fun invoke(
        value: BigDecimal
    ) {
        created = value
    }
}
