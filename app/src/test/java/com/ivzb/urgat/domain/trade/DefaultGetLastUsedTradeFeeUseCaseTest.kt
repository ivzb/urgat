package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class DefaultGetLastUsedTradeFeeUseCaseTest {

    private lateinit var tradeDao: TradeDao
    private lateinit var useCase: GetLastUsedTradeFeeUseCase

    @Before
    fun setUp() {
        tradeDao = mockk(relaxed = true)
        useCase = DefaultGetLastUsedTradeFeeUseCase(tradeDao)
    }

    @Test
    fun `invoke should call dao getLastUsedFee`() = runTest {
        useCase()

        coVerify(exactly = 1) { tradeDao.getLastUsedFee() }
    }

    @Test
    fun `invoke should return fee when available`() = runTest {
        coEvery { tradeDao.getLastUsedFee() } returns "1.50"

        val result = useCase()

        assertEquals("1.50", result)
    }

    @Test
    fun `invoke should return null when no fee available`() = runTest {
        coEvery { tradeDao.getLastUsedFee() } returns null

        val result = useCase()

        assertNull(result)
    }
}

