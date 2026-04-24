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

class DefaultGetLastUsedTradeNameUseCaseTest {

    private lateinit var tradeDao: TradeDao
    private lateinit var useCase: GetLastUsedTradeNameUseCase

    @Before
    fun setUp() {
        tradeDao = mockk(relaxed = true)
        useCase = DefaultGetLastUsedTradeNameUseCase(tradeDao)
    }

    @Test
    fun `invoke should call dao getLastUsedName`() = runTest {
        useCase()

        coVerify(exactly = 1) { tradeDao.getLastUsedName() }
    }

    @Test
    fun `invoke should return name when available`() = runTest {
        coEvery { tradeDao.getLastUsedName() } returns "AAPL"

        val result = useCase()

        assertEquals("AAPL", result)
    }

    @Test
    fun `invoke should return null when no name available`() = runTest {
        coEvery { tradeDao.getLastUsedName() } returns null

        val result = useCase()

        assertNull(result)
    }
}

