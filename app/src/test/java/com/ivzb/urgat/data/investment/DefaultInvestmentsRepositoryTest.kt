package com.ivzb.urgat.data.investment

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import java.math.BigDecimal
import kotlin.test.assertFailsWith
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultInvestmentsRepositoryTest : DefaultInvestmentsRepositoryTestBase() {

    @Test
    fun `create adds new investment and returns its id`() = runTest {
        val id = repository.create(
            name = "SNP 500",
            shares = BigDecimal("350"),
            costPrice = BigDecimal("200"),
            category = "etfs",
            currency = "USD",
            date = 1L,
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("SNP 500", stored?.name)
        assertEquals(BigDecimal("350"), stored?.shares)
        assertEquals(BigDecimal("200"), stored?.cost)
        assertEquals("etfs", stored?.category)
        assertEquals("USD", stored?.currency)
        assertEquals(1L, stored?.date)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct investment`() = runTest {
        val id = repository.create(
            name = "SNP 500",
            shares = BigDecimal("350"),
            costPrice = BigDecimal("200"),
            category = "etfs",
            currency = "USD",
            date = 1L,
        )

        val investment = repository.get(id)
        assertNotNull(investment)
        assertEquals("SNP 500", investment?.name)
        assertEquals(BigDecimal("350"), investment?.shares)
        assertEquals(BigDecimal("200"), investment?.costPrice)
        assertEquals("etfs", investment?.category)
        assertEquals("USD", investment?.currency)
        assertEquals(1L, investment?.date)
    }

    @Test
    fun `update modifies existing investment`() = runTest {
        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1L)

        repository.update(id, "S&P 500", 550.toBigDecimal(), 250.toBigDecimal(), "etfs", "EUR", 2L)

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("S&P 500", updated?.name)
        assertEquals(BigDecimal("550"), updated?.shares)
        assertEquals(BigDecimal("250"), updated?.costPrice)
        assertEquals("etfs", updated?.category)
        assertEquals("EUR", updated?.currency)
        assertEquals(2L, updated?.date)
    }

    @Test
    fun `update throws when investment does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "S&P 500", 550.toBigDecimal(), 250.toBigDecimal(), "etfs", "EUR", 2)
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes investment`() = runTest {
        val id = repository.create("Temp", BigDecimal.ONE, BigDecimal.ONE, "etfs", "USD", 1)
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `observe emits mapped investments`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(listOf("SNP 500", "BRK.B"), result.map { it.name })
    }

    @Test
    fun `observe with searchQuery filters correctly`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "SNP").first()

        assertEquals(1, result.size)
        assertEquals("SNP 500", result.first().name)
    }

    @Test
    fun `observe populates description from fetchPrice`() = runTest {
        givenMarketPrice("SNP 500", "S&P 500 ETF", "450.00")

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertEquals("S&P 500 ETF", result.first().description)
    }

    @Test
    fun `observe returns null description when fetchPrice returns null`() = runTest {
        fakeMarketPriceRepository.shouldThrow = true

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertNull(result.first().description)
    }

    @Test
    fun `observe calls fetchPrice for each investment`() = runTest {
        givenMarketPrice("SNP 500", "S&P 500", "450")
        givenMarketPrice("BRK.B", "Berkshire", "380")

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val fetchCountBefore = fakeMarketPriceRepository.fetchCount

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(2, fakeMarketPriceRepository.fetchCount - fetchCountBefore)
        assertEquals("S&P 500", result.find { it.name == "SNP 500" }?.description)
        assertEquals("Berkshire", result.find { it.name == "BRK.B" }?.description)
    }

    @Test
    fun `observe populates marketPrice from calculateMarketPrice`() = runTest {
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450.00")

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertEquals(BigDecimal("450.00"), result.first().marketPrice)
    }

    @Test
    fun `observe falls back to costPrice when calculateMarketPrice returns zero`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        assertEquals(BigDecimal.ZERO, result.first().marketPrice)
    }

    @Test
    fun `observe with searchQuery populates description and marketPrice`() = runTest {
        givenMarketPrice("SNP 500", "S&P 500 ETF", "450")
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450.00")

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "SNP").first()

        assertEquals(1, result.size)
        assertEquals("S&P 500 ETF", result.first().description)
        assertEquals(BigDecimal("450.00"), result.first().marketPrice)
    }

    @Test
    fun `observe handles partial fetchPrice availability`() = runTest {
        givenMarketPrice("SNP 500", "S&P 500", "450")
        fakeMarketPriceRepository.failingSymbols.add("BRK.B")

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        val snp = result.first { it.name == "SNP 500" }
        val brk = result.first { it.name == "BRK.B" }
        assertEquals("S&P 500", snp.description)
        assertNull(brk.description)
    }

    @Test
    fun `observe handles partial calculateMarketPrice availability`() = runTest {
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450")

        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        val snp = result.first { it.name == "SNP 500" }
        val brk = result.first { it.name == "BRK.B" }
        assertEquals(BigDecimal("450"), snp.marketPrice)
        assertEquals(BigDecimal.ZERO, brk.marketPrice)
    }

    @Test
    fun `get populates description from fetchPrice`() = runTest {
        givenMarketPrice("SNP 500", "S&P 500 ETF", "450.00")

        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals("S&P 500 ETF", investment?.description)
    }

    @Test
    fun `get returns null description when fetchPrice returns null`() = runTest {
        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        fakeMarketPriceRepository.shouldThrow = true
        val investment = repository.get(id)

        assertNotNull(investment)
        assertNull(investment?.description)
    }

    @Test
    fun `get populates marketPrice from calculateMarketPrice`() = runTest {
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450.00")

        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal("450.00"), investment?.marketPrice)
    }

    @Test
    fun `get falls back to costPrice when calculateMarketPrice returns null`() = runTest {
        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals(BigDecimal.ZERO, investment?.marketPrice)
    }

    @Test
    fun `get calls fetchPrice exactly once`() = runTest {
        givenMarketPrice("SNP 500", "S&P 500", "450")

        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        val fetchCountBefore = fakeMarketPriceRepository.fetchCount

        repository.get(id)

        assertEquals(1, fakeMarketPriceRepository.fetchCount - fetchCountBefore)
    }

    @Test
    fun `get populates both description and marketPrice together`() = runTest {
        givenMarketPrice("SNP 500", "S&P 500 ETF", "450")
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("460.00")

        val id = repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        val investment = repository.get(id)

        assertNotNull(investment)
        assertEquals("S&P 500 ETF", investment?.description)
        assertEquals(BigDecimal("460.00"), investment?.marketPrice)
    }
}
