package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradeDao
import com.ivzb.urgat.data.trade.TradeEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultGetTradeUseCaseTest {

    private lateinit var tradeDao: TradeDao
    private lateinit var getUseCase: GetTradeUseCase

    @Before
    fun setUp() {
        tradeDao = mockk()
        getUseCase = DefaultGetTradeUseCase(tradeDao)
    }

    @Test
    fun `invoke should return trade when dao returns entity`() = runTest {
        val id = "123"
        val entity = TradeEntity(
            id = id,
            name = "AAPL",
            amount = BigDecimal("150.25"),
            currency = "USD",
            date = 1731268800000L,
            isDeleted = false,
            createdAt = 1699920000000L,
        )
        coEvery { tradeDao.getById(id) } returns entity

        val result = getUseCase(id)

        val expected = Trade(
            id = id,
            name = "AAPL",
            amount = BigDecimal("150.25"),
            currency = "USD",
            date = 1731268800000L,
            isDeleted = false,
            createdAt = 1699920000000L,
        )

        assertEquals(expected, result)
        coVerify(exactly = 1) { tradeDao.getById(id) }
    }

    @Test
    fun `invoke should return null when dao returns null`() = runTest {
        val id = "999"
        coEvery { tradeDao.getById(id) } returns null

        val result = getUseCase(id)

        assertNull(result)
        coVerify(exactly = 1) { tradeDao.getById(id) }
    }

}

