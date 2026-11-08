package com.ivzb.urgat.data.watchlist

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
class DefaultWatchlistAssetsRepositoryTest : DefaultWatchlistAssetsRepositoryTestBase() {

    @Test
    fun `create adds new watchlist asset and returns its id`() = runTest {
        val id = repository.create(
            name = "AAPL",
        )

        val stored = dao.getById(id)
        assertNotNull(stored)
        assertEquals("AAPL", stored?.name)
        assertTrue(stored?.isDeleted == false)
    }

    @Test
    fun `get retrieves correct watchlist asset`() = runTest {
        val id = repository.create(
            name = "AAPL",
        )

        val watchlistAsset = repository.get(id)
        assertNotNull(watchlistAsset)
        assertEquals("AAPL", watchlistAsset?.name)
    }

    @Test
    fun `get returns null for non-existing watchlist asset`() = runTest {
        val watchlistAsset = repository.get("non-existing-id")
        assertNull(watchlistAsset)
    }

    @Test
    fun `update modifies existing watchlist asset`() = runTest {
        val id = repository.create("AAPL")

        repository.update(id, "GOOGL")

        val updated = repository.get(id)
        assertNotNull(updated)
        assertEquals("GOOGL", updated?.name)
    }

    @Test
    fun `update throws when watchlist asset does not exist`() = runTest {
        val exception = assertFailsWith<Exception> {
            repository.update("missing", "GOOGL")
        }

        assertTrue(exception.message?.contains("not found") == true)
    }

    @Test
    fun `delete removes watchlist asset`() = runTest {
        val id = repository.create("AAPL")
        assertNotNull(dao.getById(id))

        repository.delete(id)
        assertNull(dao.getById(id))
    }

    @Test
    fun `observe emits mapped watchlist assets`() = runTest {
        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(listOf("AAPL", "GOOGL"), result.map { it.name })
    }

    @Test
    fun `observe with searchQuery filters correctly`() = runTest {
        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").first()

        assertEquals(1, result.size)
        assertEquals("AAPL", result.first().name)
    }

    @Test
    fun `observe populates market price from fetchPrice`() = runTest {
        givenMarketPrice("AAPL", "Apple Inc.", price = "150.00", previousClosePrice = "148.00")

        repository.create("AAPL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        val asset = result.first()
        assertEquals("Apple Inc.", asset.description)
        assertEquals(BigDecimal("150.00"), asset.marketPrice)
        assertEquals("USD", asset.currency)
        assertEquals(BigDecimal("148.00"), asset.previousClosePrice)
        assertEquals(2, asset.pricePrecision)
    }

    @Test
    fun `observe returns null market data when fetchPrice returns null`() = runTest {
        fakeMarketPriceRepository.shouldThrow = true

        repository.create("AAPL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(1, result.size)
        val asset = result.first()
        assertEquals("AAPL", asset.name)
        assertNull(asset.description)
        assertNull(asset.marketPrice)
        assertNull(asset.currency)
        assertNull(asset.previousClosePrice)
    }

    @Test
    fun `observe calls fetchPrice for each asset`() = runTest {
        givenMarketPrice("AAPL", "Apple", price = "150", previousClosePrice = "148")
        givenMarketPrice("GOOGL", "Google", price = "2800", previousClosePrice = "2790")

        repository.create("AAPL")
        repository.create("GOOGL")

        val fetchCountBefore = fakeMarketPriceRepository.fetchCount

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        assertEquals(2, fakeMarketPriceRepository.fetchCount - fetchCountBefore)
        assertEquals("Apple", result.find { it.name == "AAPL" }?.description)
        assertEquals("Google", result.find { it.name == "GOOGL" }?.description)
    }

    @Test
    fun `observe with searchQuery populates market price`() = runTest {
        givenMarketPrice("AAPL", "Apple Inc.", price = "150.00", previousClosePrice = "148.00")

        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").first()

        assertEquals(1, result.size)
        assertEquals("Apple Inc.", result.first().description)
        assertEquals(BigDecimal("150.00"), result.first().marketPrice)
        assertEquals(2, result.first().pricePrecision)
    }

    @Test
    fun `observe handles partial market price availability`() = runTest {
        givenMarketPrice("AAPL", "Apple", price = "150", previousClosePrice = "148")
        fakeMarketPriceRepository.failingSymbols.add("GOOGL")

        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)
        val apple = result.first { it.name == "AAPL" }
        val google = result.first { it.name == "GOOGL" }
        assertEquals("Apple", apple.description)
        assertEquals(BigDecimal("150"), apple.marketPrice)
        assertNull(google.description)
        assertNull(google.marketPrice)
    }

    @Test
    fun `get populates market price from fetchPrice`() = runTest {
        givenMarketPrice("AAPL", "Apple Inc.", price = "150.00", previousClosePrice = "148.00")

        val id = repository.create("AAPL")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("Apple Inc.", asset?.description)
        assertEquals(BigDecimal("150.00"), asset?.marketPrice)
        assertEquals("USD", asset?.currency)
        assertEquals(BigDecimal("148.00"), asset?.previousClosePrice)
    }

    @Test
    fun `get returns null market data when fetchPrice returns null`() = runTest {
        val id = repository.create("AAPL")

        fakeMarketPriceRepository.shouldThrow = true
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("AAPL", asset?.name)
        assertNull(asset?.description)
        assertNull(asset?.marketPrice)
        assertNull(asset?.currency)
        assertNull(asset?.previousClosePrice)
    }

    @Test
    fun `get calls fetchPrice exactly once`() = runTest {
        givenMarketPrice("AAPL", "Apple", price = "150", previousClosePrice = "148")

        val id = repository.create("AAPL")
        val fetchCountBefore = fakeMarketPriceRepository.fetchCount

        repository.get(id)

        assertEquals(1, fakeMarketPriceRepository.fetchCount - fetchCountBefore)
    }
}
