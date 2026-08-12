package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetLastUsedTradeCurrencyUseCaseTest {

    private lateinit var tradeDao: TradeDao
    private lateinit var useCase: GetLastUsedTradeCurrencyUseCase

    @Before
    fun setUp() {
        tradeDao = mockk(relaxed = true)
        useCase = DefaultGetLastUsedTradeCurrencyUseCase(tradeDao)
    }

    @Test
    fun `invoke should call dao getLastUsedCurrency`() = runTest {
        useCase()

        coVerify(exactly = 1) { tradeDao.getLastUsedCurrency() }
    }

    @Test
    fun `invoke should return currency when available`() = runTest {
        coEvery { tradeDao.getLastUsedCurrency() } returns "USD"

        val result = useCase()

        assertEquals("USD", result)
    }

    @Test
    fun `invoke should return null when no currency available`() = runTest {
        coEvery { tradeDao.getLastUsedCurrency() } returns null

        val result = useCase()

        assertNull(result)
    }

}

