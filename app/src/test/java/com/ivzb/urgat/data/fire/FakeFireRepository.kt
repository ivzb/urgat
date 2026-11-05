package com.ivzb.urgat.data.fire

import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_DATE_RANGE_TYPE
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_RETURN_OF_INVESTMENT
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_TAX_RATE
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_WITHDRAWAL_RATE
import com.ivzb.urgat.ui.fire.FireDateRangeType
import java.math.BigDecimal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFireRepository : FireRepository {

    private val _withdrawalRateFlow = MutableStateFlow<BigDecimal?>(DEFAULT_FIRE_WITHDRAWAL_RATE)
    override val withdrawalRateFlow: Flow<BigDecimal?> = _withdrawalRateFlow

    override suspend fun updateWithdrawalRate(value: String) {
        _withdrawalRateFlow.value = value.toBigDecimalOrNull()
    }

    private val _returnOfInvestmentFlow = MutableStateFlow<BigDecimal?>(DEFAULT_FIRE_RETURN_OF_INVESTMENT)
    override val returnOfInvestmentFlow: Flow<BigDecimal?> = _returnOfInvestmentFlow

    override suspend fun updateReturnOfInvestment(value: String) {
        _returnOfInvestmentFlow.value = value.toBigDecimalOrNull()
    }

    private val _taxRateFlow = MutableStateFlow<BigDecimal?>(DEFAULT_FIRE_TAX_RATE)
    override val taxRateFlow: Flow<BigDecimal?> = _taxRateFlow

    override suspend fun updateTaxRate(value: String) {
        _taxRateFlow.value = value.toBigDecimalOrNull()
    }

    private val _fireDateRangeTypeFlow = MutableStateFlow(DEFAULT_FIRE_DATE_RANGE_TYPE)
    override val fireDateRangeTypeFlow: Flow<FireDateRangeType> = _fireDateRangeTypeFlow

    override suspend fun updateFireDateRangeType(value: FireDateRangeType) {
        _fireDateRangeTypeFlow.value = value
    }

    private val _fireCustomStartFlow = MutableStateFlow(0L)
    override val fireCustomStartFlow: Flow<Long> = _fireCustomStartFlow

    override suspend fun updateFireCustomStart(value: Long) {
        _fireCustomStartFlow.value = value
    }

    private val _fireCustomEndFlow = MutableStateFlow(0L)
    override val fireCustomEndFlow: Flow<Long> = _fireCustomEndFlow

    override suspend fun updateFireCustomEnd(value: Long) {
        _fireCustomEndFlow.value = value
    }

}
