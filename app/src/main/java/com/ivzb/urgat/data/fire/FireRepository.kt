package com.ivzb.urgat.data.fire

import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface FireRepository {

    val withdrawalRateFlow: Flow<BigDecimal?>

    val returnOfInvestmentFlow: Flow<BigDecimal?>

    val taxRateFlow: Flow<BigDecimal?>

    suspend fun updateWithdrawalRate(value: String)

    suspend fun updateReturnOfInvestment(value: String)

    suspend fun updateTaxRate(value: String)

    companion object {

        val DEFAULT_FIRE_WITHDRAWAL_RATE = 4.toBigDecimal()

        val DEFAULT_FIRE_RETURN_OF_INVESTMENT = 7.toBigDecimal()

        val DEFAULT_FIRE_TAX_RATE = 10.toBigDecimal()

    }

}