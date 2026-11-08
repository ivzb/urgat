package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultInvestmentsRepositoryMappingTest : DefaultInvestmentsRepositoryTestBase() {

    @Test
    fun `toExternal maps entity cost field to costPrice`() = runTest {
        val id = repository.create("TEST", BigDecimal("100"), BigDecimal("55.75"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal("55.75"), investment?.costPrice)
    }

    @Test
    fun `toExternal maps all entity fields correctly`() = runTest {
        val id = repository.create(
            name = "VUSA.L",
            shares = BigDecimal("123.456"),
            costPrice = BigDecimal("78.90"),
            category = "stocks",
            currency = "GBP",
            date = 1700000000L,
        )

        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(id, investment?.id)
        assertEquals("VUSA.L", investment?.name)
        assertEquals(BigDecimal("123.456"), investment?.shares)
        assertEquals(BigDecimal("78.90"), investment?.costPrice)
        assertEquals("stocks", investment?.category)
        assertEquals("GBP", investment?.currency)
        assertEquals(1700000000L, investment?.date)
        assertEquals(false, investment?.isDeleted)
    }

    @Test
    fun `toExternal propagates pricePrecision from market data`() = runTest {
        givenMarketPrice("AAPL", "Apple Inc.", "190.50", pricePrecision = 4)

        val id = repository.create("AAPL", BigDecimal("10"), BigDecimal("150"), "stocks", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(4, investment?.pricePrecision)
    }

    @Test
    fun `toExternal sets pricePrecision to null when no market data`() = runTest {
        fakeMarketPriceRepository.shouldThrow = true

        val id = repository.create("UNKNOWN", BigDecimal("10"), BigDecimal("100"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertNull(investment?.pricePrecision)
    }

    @Test
    fun `toExternal uses calculateMarketPrice result as marketPrice`() = runTest {
        fakeInvestmentMarketPriceRepository.prices["TSLA"] = BigDecimal("250.00")

        val id = repository.create("TSLA", BigDecimal("5"), BigDecimal("200"), "stocks", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal("250.00"), investment?.marketPrice)
    }

    @Test
    fun `toExternal falls back to zero marketPrice when calculateMarketPrice returns zero`() = runTest {
        val id = repository.create("NOPRICE", BigDecimal("10"), BigDecimal("100"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal.ZERO, investment?.marketPrice)
    }

    @Test
    fun `toExternal preserves isDeleted as false for new investment`() = runTest {
        val id = repository.create("TEST", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(false, investment?.isDeleted)
    }

    @Test
    fun `toExternal preserves createdAt timestamp`() = runTest {
        val id = repository.create("TEST", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)

        val entity = dao.getById(id)
        val investment = repository.get(id)

        assertNotNull(entity)
        assertNotNull(investment)
        assertEquals(entity?.createdAt, investment?.createdAt)
    }

    @Test
    fun `toExternal handles investment name with special characters`() = runTest {
        val id = repository.create("BRK.B", BigDecimal("10"), BigDecimal("400"), "stocks", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals("BRK.B", investment?.name)
    }

    @Test
    fun `toExternal handles zero shares`() = runTest {
        val id = repository.create("TEST", BigDecimal.ZERO, BigDecimal("100"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal.ZERO, investment?.shares)
    }

    @Test
    fun `toExternal handles very large shares and cost values`() = runTest {
        val largeShares = BigDecimal("9999999999.999999")
        val largeCost = BigDecimal("99999.99")

        val id = repository.create("BIG", largeShares, largeCost, "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(largeShares, investment?.shares)
        assertEquals(largeCost, investment?.costPrice)
    }

    @Test
    fun `toExternal handles very small fractional cost`() = runTest {
        val smallCost = BigDecimal("0.0001")

        val id = repository.create("MICRO", BigDecimal("1000000"), smallCost, "crypto", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(smallCost, investment?.costPrice)
    }

    @Test
    fun `toExternal maps different categories correctly`() = runTest {
        val idEtf = repository.create("ETF1", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        val idStock = repository.create("STK1", BigDecimal("1"), BigDecimal("1"), "stocks", "USD", 2L)
        val idCrypto = repository.create("CRY1", BigDecimal("1"), BigDecimal("1"), "crypto", "USD", 3L)

        assertEquals("etfs", repository.get(idEtf)?.category)
        assertEquals("stocks", repository.get(idStock)?.category)
        assertEquals("crypto", repository.get(idCrypto)?.category)
    }

    @Test
    fun `toExternal maps different currencies correctly`() = runTest {
        val idUsd = repository.create("A", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        val idEur = repository.create("B", BigDecimal("1"), BigDecimal("1"), "etfs", "EUR", 2L)
        val idGbp = repository.create("C", BigDecimal("1"), BigDecimal("1"), "etfs", "GBP", 3L)

        assertEquals("USD", repository.get(idUsd)?.currency)
        assertEquals("EUR", repository.get(idEur)?.currency)
        assertEquals("GBP", repository.get(idGbp)?.currency)
    }

    @Test
    fun `toExternal sets description to null when market data unavailable`() = runTest {
        val id = repository.create("NODATA", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertNull(investment?.description)
    }

    @Test
    fun `observe toExternal maps all fields for each investment in list`() = runTest {
        givenMarketPrice("A", "Alpha", "100")
        givenMarketPrice("B", "Beta", "200", pricePrecision = 3)
        fakeInvestmentMarketPriceRepository.prices["A"] = BigDecimal("105")
        fakeInvestmentMarketPriceRepository.prices["B"] = BigDecimal("210")

        repository.create("A", BigDecimal("10"), BigDecimal("90"), "stocks", "USD", 1L)
        repository.create("B", BigDecimal("20"), BigDecimal("180"), "etfs", "EUR", 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)

        val a = result.first { it.name == "A" }
        assertEquals("Alpha", a.description)
        assertEquals(BigDecimal("105"), a.marketPrice)
        assertEquals(2, a.pricePrecision)
        assertEquals(BigDecimal("10"), a.shares)
        assertEquals(BigDecimal("90"), a.costPrice)
        assertEquals("stocks", a.category)
        assertEquals("USD", a.currency)
        assertEquals(1L, a.date)
        assertEquals(false, a.isDeleted)

        val b = result.first { it.name == "B" }
        assertEquals("Beta", b.description)
        assertEquals(BigDecimal("210"), b.marketPrice)
        assertEquals(3, b.pricePrecision)
        assertEquals(BigDecimal("20"), b.shares)
        assertEquals(BigDecimal("180"), b.costPrice)
        assertEquals("etfs", b.category)
        assertEquals("EUR", b.currency)
        assertEquals(2L, b.date)
        assertEquals(false, b.isDeleted)
    }

    @Test
    fun `observe toExternal handles mixed market data availability`() = runTest {
        givenMarketPrice("A", "Alpha", "100")
        fakeMarketPriceRepository.failingSymbols.add("B")
        fakeInvestmentMarketPriceRepository.prices["A"] = BigDecimal("105")

        repository.create("A", BigDecimal("10"), BigDecimal("90"), "stocks", "USD", 1L)
        repository.create("B", BigDecimal("20"), BigDecimal("180"), "etfs", "EUR", 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        val a = result.first { it.name == "A" }
        assertEquals("Alpha", a.description)
        assertEquals(BigDecimal("105"), a.marketPrice)
        assertEquals(2, a.pricePrecision)

        val b = result.first { it.name == "B" }
        assertNull(b.description)
        assertEquals(BigDecimal.ZERO, b.marketPrice)
        assertNull(b.pricePrecision)
    }

    @Test
    fun `toExternal preserves entity id through mapping`() = runTest {
        val id = repository.create("TEST", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)

        val entity = dao.getById(id)
        val investment = repository.get(id)

        assertNotNull(entity)
        assertNotNull(investment)
        assertEquals(entity?.id, investment?.id)
    }

    @Test
    fun `toExternal after update reflects updated fields`() = runTest {
        val id = repository.create("OLD", BigDecimal("10"), BigDecimal("100"), "etfs", "USD", 1L)

        repository.update(id, "NEW", BigDecimal("20"), BigDecimal("200"), "stocks", "EUR", 2L)

        val investment = repository.get(id)
        assertNotNull(investment)
        assertEquals(id, investment?.id)
        assertEquals("NEW", investment?.name)
        assertEquals(BigDecimal("20"), investment?.shares)
        assertEquals(BigDecimal("200"), investment?.costPrice)
        assertEquals("stocks", investment?.category)
        assertEquals("EUR", investment?.currency)
        assertEquals(2L, investment?.date)
    }

    @Test
    fun `observe toExternal with date zero`() = runTest {
        repository.create("TEST", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 0L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertEquals(0L, result.first().date)
    }

    @Test
    fun `observe toExternal preserves order from dao`() = runTest {
        repository.create("C", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 3L)
        repository.create("A", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        repository.create("B", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 2L)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(3, result.size)
        assertEquals(listOf("C", "A", "B"), result.map { it.name })
    }

    @Test
    fun `toExternal with pricePrecision zero`() = runTest {
        givenMarketPrice("ROUND", "Rounded", "100", pricePrecision = 0)

        val id = repository.create("ROUND", BigDecimal("1"), BigDecimal("1"), "etfs", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(0, investment?.pricePrecision)
    }

    @Test
    fun `toExternal with high pricePrecision`() = runTest {
        givenMarketPrice("PRECISE", "High Precision", "0.00000001", pricePrecision = 8)

        val id = repository.create("PRECISE", BigDecimal("1000000"), BigDecimal("0.00000001"), "crypto", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(8, investment?.pricePrecision)
        assertEquals("High Precision", investment?.description)
    }

    @Test
    fun `toExternal description comes from market data not entity`() = runTest {
        givenMarketPrice("TICKER", "Full Company Name", "100")

        val id = repository.create("TICKER", BigDecimal("1"), BigDecimal("50"), "stocks", "USD", 1L)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals("TICKER", investment?.name)
        assertEquals("Full Company Name", investment?.description)
    }

    @Test
    fun `observe toExternal empty list returns empty`() = runTest {
        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertTrue(result.isEmpty())
    }
}
