package com.ivzb.urgat.data.fire

import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface FireRepository {

    val withdrawalRateFlow: Flow<BigDecimal?>

    suspend fun updateWithdrawalRate(value: String)

}