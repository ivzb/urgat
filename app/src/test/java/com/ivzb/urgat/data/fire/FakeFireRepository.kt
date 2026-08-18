package com.ivzb.urgat.data.fire

import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_DATE_RANGE_TYPE
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_RETURN_OF_INVESTMENT
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_TAX_RATE
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_WITHDRAWAL_RATE
import com.ivzb.urgat.ui.fire.FireDateRangeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.math.BigDecimal

class FakeFireRepository : FireRepository {

    private val _withdrawalRate = MutableStateFlow<BigDecimal?>(DEFAULT_FIRE_WITHDRAWAL_RATE)
    override val withdrawalRateFlow: Flow<BigDecimal?> = _withdrawalRate

    override suspend fun updateWithdrawalRate(value: String) {
        _withdrawalRate.value = value.toBigDecimalOrNull()
    }

    private val _returnOfInvestment = MutableStateFlow<BigDecimal?>(DEFAULT_FIRE_RETURN_OF_INVESTMENT)
    override val returnOfInvestmentFlow: Flow<BigDecimal?> = _returnOfInvestment

    override suspend fun updateReturnOfInvestment(value: String) {
        _returnOfInvestment.value = value.toBigDecimalOrNull()
    }

    private val _taxRate = MutableStateFlow<BigDecimal?>(DEFAULT_FIRE_TAX_RATE)
    override val taxRateFlow: Flow<BigDecimal?> = _taxRate

    override suspend fun updateTaxRate(value: String) {
        _taxRate.value = value.toBigDecimalOrNull()
    }

    private val _fireDateRangeType = MutableStateFlow(DEFAULT_FIRE_DATE_RANGE_TYPE)
    override val fireDateRangeTypeFlow: Flow<FireDateRangeType> = _fireDateRangeType

    override suspend fun updateFireDateRangeType(value: FireDateRangeType) {
        _fireDateRangeType.value = value
    }

    private val _fireCustomStart = MutableStateFlow(0L)
    override val fireCustomStartFlow: Flow<Long> = _fireCustomStart

    override suspend fun updateFireCustomStart(value: Long) {
        _fireCustomStart.value = value
    }

    private val _fireCustomEnd = MutableStateFlow(0L)
    override val fireCustomEndFlow: Flow<Long> = _fireCustomEnd

    override suspend fun updateFireCustomEnd(value: Long) {
        _fireCustomEnd.value = value
    }

}

