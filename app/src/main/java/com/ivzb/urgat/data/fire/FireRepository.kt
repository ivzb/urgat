package com.ivzb.urgat.data.fire

import com.ivzb.urgat.ui.fire.FireDateRangeType
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface FireRepository {

    val withdrawalRateFlow: Flow<BigDecimal?>
    suspend fun updateWithdrawalRate(value: String)

    val returnOfInvestmentFlow: Flow<BigDecimal?>
    suspend fun updateReturnOfInvestment(value: String)

    val taxRateFlow: Flow<BigDecimal?>
    suspend fun updateTaxRate(value: String)

    val fireDateRangeTypeFlow: Flow<FireDateRangeType>
    suspend fun updateFireDateRangeType(value: FireDateRangeType)

    val fireCustomStartFlow: Flow<Long>
    suspend fun updateFireCustomStart(value: Long)

    val fireCustomEndFlow: Flow<Long>
    suspend fun updateFireCustomEnd(value: Long)

    companion object {

        val DEFAULT_FIRE_WITHDRAWAL_RATE = 4.toBigDecimal()

        val DEFAULT_FIRE_RETURN_OF_INVESTMENT = 7.toBigDecimal()

        val DEFAULT_FIRE_TAX_RATE = 10.toBigDecimal()

        val DEFAULT_FIRE_DATE_RANGE_TYPE = FireDateRangeType.LAST_MONTH

    }

}