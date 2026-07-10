package com.ivzb.urgat.data.fire

import com.ivzb.urgat.data.dataStore.FakePreferencesDataStore
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_DATE_RANGE_TYPE
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_RETURN_OF_INVESTMENT
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_TAX_RATE
import com.ivzb.urgat.data.fire.FireRepository.Companion.DEFAULT_FIRE_WITHDRAWAL_RATE
import com.ivzb.urgat.ui.fire.FireDateRangeType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultFireRepositoryTest {

    private lateinit var dataStore: FakePreferencesDataStore
    private lateinit var repository: DefaultFireRepository

    @Before
    fun setup() {
        dataStore = FakePreferencesDataStore()
        repository = DefaultFireRepository(dataStore)
    }

    @Test
    fun `withdrawalRateFlow emits default value when not set`() = runTest {
        val value = repository.withdrawalRateFlow.first()

        assertEquals(DEFAULT_FIRE_WITHDRAWAL_RATE, value)
    }

    @Test
    fun `returnOfInvestmentFlow emits default value when not set`() = runTest {
        val value = repository.returnOfInvestmentFlow.first()

        assertEquals(DEFAULT_FIRE_RETURN_OF_INVESTMENT, value)
    }

    @Test
    fun `taxRateFlow emits default value when not set`() = runTest {
        val value = repository.taxRateFlow.first()

        assertEquals(DEFAULT_FIRE_TAX_RATE, value)
    }

    @Test
    fun `updateWithdrawalRate updates flow value`() = runTest {
        repository.updateWithdrawalRate("4.5")

        val value = repository.withdrawalRateFlow.first()

        assertEquals(BigDecimal("4.5"), value)
    }

    @Test
    fun `updateReturnOfInvestment updates flow value`() = runTest {
        repository.updateReturnOfInvestment("7.25")

        val value = repository.returnOfInvestmentFlow.first()

        assertEquals(BigDecimal("7.25"), value)
    }

    @Test
    fun `updateTaxRate updates flow value`() = runTest {
        repository.updateTaxRate("12.75")

        val value = repository.taxRateFlow.first()

        assertEquals(BigDecimal("12.75"), value)
    }

    @Test
    fun `invalid BigDecimal value emits null`() = runTest {
        repository.updateWithdrawalRate("not_a_number")

        val value = repository.withdrawalRateFlow.first()

        assertEquals(null, value)
    }

    @Test
    fun `fireDateRangeTypeFlow emits default value when not set`() = runTest {
        val value = repository.fireDateRangeTypeFlow.first()

        assertEquals(DEFAULT_FIRE_DATE_RANGE_TYPE, value)
    }

    @Test
    fun `updateFireDateRangeType updates flow value to YTD`() = runTest {
        repository.updateFireDateRangeType(FireDateRangeType.YTD)

        val value = repository.fireDateRangeTypeFlow.first()

        assertEquals(FireDateRangeType.YTD, value)
    }

    @Test
    fun `updateFireDateRangeType updates flow value to CUSTOM`() = runTest {
        repository.updateFireDateRangeType(FireDateRangeType.CUSTOM)

        val value = repository.fireDateRangeTypeFlow.first()

        assertEquals(FireDateRangeType.CUSTOM, value)
    }

    @Test
    fun `updateFireDateRangeType updates flow value to LAST_YEAR`() = runTest {
        repository.updateFireDateRangeType(FireDateRangeType.LAST_YEAR)

        val value = repository.fireDateRangeTypeFlow.first()

        assertEquals(FireDateRangeType.LAST_YEAR, value)
    }

    @Test
    fun `fireCustomStartFlow emits default value when not set`() = runTest {
        val value = repository.fireCustomStartFlow.first()

        assertEquals(0L, value)
    }

    @Test
    fun `updateFireCustomStart updates flow value`() = runTest {
        val expected = 1704067200000L

        repository.updateFireCustomStart(expected)

        val value = repository.fireCustomStartFlow.first()

        assertEquals(expected, value)
    }

    @Test
    fun `fireCustomEndFlow emits default value when not set`() = runTest {
        val value = repository.fireCustomEndFlow.first()

        assertEquals(0L, value)
    }

    @Test
    fun `updateFireCustomEnd updates flow value`() = runTest {
        val expected = 1735689600000L

        repository.updateFireCustomEnd(expected)

        val value = repository.fireCustomEndFlow.first()

        assertEquals(expected, value)
    }
}
