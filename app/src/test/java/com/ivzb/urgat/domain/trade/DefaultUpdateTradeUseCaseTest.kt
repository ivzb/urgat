package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import com.ivzb.urgat.data.trade.TradeEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class DefaultUpdateTradeUseCaseTest {

    private lateinit var tradeDao: TradeDao
    private lateinit var updateUseCase: UpdateTradeUseCase

    @Before
    fun setUp() {
        tradeDao = mockk(relaxed = true)
        updateUseCase = DefaultUpdateTradeUseCase(tradeDao)
    }

    @Test
    fun `invoke should call dao upsert with correct parameters`() = runTest {
        val id = "123"
        val name = "TSLA"
        val amount = BigDecimal("250.50")
        val currency = "EUR"
        val date = 1731268800000L
        val entity = slot<TradeEntity>()

        coEvery { tradeDao.getById(id) } returns existing

        updateUseCase(id, name, amount, currency, date)

        coVerify(exactly = 1) { tradeDao.getById(id) }
        coVerify(exactly = 1) { tradeDao.upsert(capture(entity)) }

        val expected = TradeEntity(
            id = id,
            name = name,
            amount = amount,
            currency = currency,
            date = date,
            isDeleted = false,
            createdAt = 1699920000000L,
        )

        assertEquals(expected, entity.captured)
    }

    @Test
    fun `invoke should preserve isDeleted and createdAt of the existing trade`() = runTest {
        val id = "123"
        val entity = slot<TradeEntity>()

        coEvery { tradeDao.getById(id) } returns existing.copy(isDeleted = true, createdAt = 42L)

        updateUseCase(id, "TSLA", BigDecimal("250.50"), "EUR", 1731268800000L)

        coVerify(exactly = 1) { tradeDao.upsert(capture(entity)) }

        assertEquals(true, entity.captured.isDeleted)
        assertEquals(42L, entity.captured.createdAt)
    }

    @Test
    fun `invoke should not call dao upsert when trade does not exist`() = runTest {
        val id = "999"

        coEvery { tradeDao.getById(id) } returns null

        updateUseCase(id, "TSLA", BigDecimal("250.50"), "EUR", 1731268800000L)

        coVerify(exactly = 1) { tradeDao.getById(id) }
        coVerify(exactly = 0) { tradeDao.upsert(any()) }
    }

    companion object {

        private val existing = TradeEntity(
            id = "123",
            name = "AAPL",
            amount = BigDecimal("150.25"),
            currency = "USD",
            date = 1699920000000L,
            isDeleted = false,
            createdAt = 1699920000000L,
        )

    }

}

