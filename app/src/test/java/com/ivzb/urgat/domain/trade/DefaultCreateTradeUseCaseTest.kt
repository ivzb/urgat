package com.ivzb.urgat.domain.trade

import com.ivzb.urgat.data.trade.TradeDao
import com.ivzb.urgat.data.trade.TradeEntity
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DefaultCreateTradeUseCaseTest {

    private lateinit var tradeDao: TradeDao
    private lateinit var createUseCase: CreateTradeUseCase

    @Before
    fun setUp() {
        tradeDao = mockk(relaxed = true)
        createUseCase = DefaultCreateTradeUseCase(tradeDao)
    }

    @Test
    fun `invoke should call dao upsert with correct parameters`() = runTest {
        val name = "AAPL"
        val amount = BigDecimal("150.25")
        val currency = "USD"
        val date = 1731268800000L
        val entity = slot<TradeEntity>()

        createUseCase(name, amount, currency, date)

        coVerify(exactly = 1) { tradeDao.upsert(capture(entity)) }

        assertEquals(name, entity.captured.name)
        assertEquals(amount, entity.captured.amount)
        assertEquals(currency, entity.captured.currency)
        assertEquals(date, entity.captured.date)
    }

    @Test
    fun `invoke should create a trade that is not deleted`() = runTest {
        val entity = slot<TradeEntity>()

        createUseCase("AAPL", BigDecimal("150.25"), "USD", 1731268800000L)

        coVerify(exactly = 1) { tradeDao.upsert(capture(entity)) }

        assertFalse(entity.captured.isDeleted)
    }

    @Test
    fun `invoke should generate a unique id for every trade`() = runTest {
        val entities = mutableListOf<TradeEntity>()

        createUseCase("AAPL", BigDecimal("150.25"), "USD", 1731268800000L)
        createUseCase("AAPL", BigDecimal("150.25"), "USD", 1731268800000L)

        coVerify(exactly = 2) { tradeDao.upsert(capture(entities)) }

        assertEquals(2, entities.size)
        assertNotEquals(entities[0].id, entities[1].id)
        assertEquals(entities[0].id, UUID.fromString(entities[0].id).toString())
        assertEquals(entities[1].id, UUID.fromString(entities[1].id).toString())
    }

    @Test
    fun `invoke should set createdAt to the current time`() = runTest {
        val entity = slot<TradeEntity>()

        val before = System.currentTimeMillis()
        createUseCase("AAPL", BigDecimal("150.25"), "USD", 1731268800000L)
        val after = System.currentTimeMillis()

        coVerify(exactly = 1) { tradeDao.upsert(capture(entity)) }

        assertTrue(entity.captured.createdAt in before..after)
    }

}

