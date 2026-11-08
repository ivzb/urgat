package com.ivzb.urgat.data.watchlist

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
class DefaultWatchlistAssetsRepositoryMappingTest : DefaultWatchlistAssetsRepositoryTestBase() {

    @Test
    fun `toExternal maps all entity fields correctly with market data`() = runTest {
        givenMarketPrice("MSFT", "Microsoft Corporation", price = "415.50", previousClosePrice = "412.00")

        val id = repository.create("MSFT")

        val entity = dao.getById(id)
        val asset = repository.get(id)

        assertNotNull(entity)
        assertNotNull(asset)
        assertEquals(entity?.id, asset?.id)
        assertEquals("MSFT", asset?.name)
        assertEquals("Microsoft Corporation", asset?.description)
        assertEquals(BigDecimal("415.50"), asset?.marketPrice)
        assertEquals("USD", asset?.currency)
        assertEquals(BigDecimal("412.00"), asset?.previousClosePrice)
        assertEquals(2, asset?.pricePrecision)
        assertEquals(false, asset?.isDeleted)
        assertEquals(entity?.createdAt, asset?.createdAt)
    }

    @Test
    fun `toExternal maps all fields to null when no market data`() = runTest {
        fakeMarketPriceRepository.shouldThrow = true

        val id = repository.create("UNKNOWN")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("UNKNOWN", asset?.name)
        assertNull(asset?.description)
        assertNull(asset?.marketPrice)
        assertNull(asset?.currency)
        assertNull(asset?.previousClosePrice)
        assertNull(asset?.pricePrecision)
    }

    @Test
    fun `toExternal preserves entity id through mapping`() = runTest {
        val id = repository.create("TEST")

        val entity = dao.getById(id)
        val asset = repository.get(id)

        assertNotNull(entity)
        assertNotNull(asset)
        assertEquals(entity?.id, asset?.id)
    }

    @Test
    fun `toExternal preserves isDeleted as false for new asset`() = runTest {
        val id = repository.create("TEST")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(false, asset?.isDeleted)
    }

    @Test
    fun `toExternal preserves createdAt timestamp`() = runTest {
        val id = repository.create("TEST")

        val entity = dao.getById(id)
        val asset = repository.get(id)

        assertNotNull(entity)
        assertNotNull(asset)
        assertEquals(entity?.createdAt, asset?.createdAt)
    }

    @Test
    fun `toExternal propagates pricePrecision from market data`() = runTest {
        givenMarketPrice("AAPL", "Apple Inc.", price = "190.50", previousClosePrice = "189.00", pricePrecision = 4)

        val id = repository.create("AAPL")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(4, asset?.pricePrecision)
    }

    @Test
    fun `toExternal sets pricePrecision to null when no market data`() = runTest {
        fakeMarketPriceRepository.shouldThrow = true

        val id = repository.create("UNKNOWN")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertNull(asset?.pricePrecision)
    }

    @Test
    fun `toExternal with pricePrecision zero`() = runTest {
        givenMarketPrice("ROUND", "Rounded Asset", price = "100", previousClosePrice = "99", currency = "JPY", pricePrecision = 0)

        val id = repository.create("ROUND")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(0, asset?.pricePrecision)
    }

    @Test
    fun `toExternal with high pricePrecision`() = runTest {
        givenMarketPrice("SHIB", "Shiba Inu", price = "0.00000850", previousClosePrice = "0.00000845", pricePrecision = 8)

        val id = repository.create("SHIB")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(8, asset?.pricePrecision)
        assertEquals(BigDecimal("0.00000850"), asset?.marketPrice)
        assertEquals(BigDecimal("0.00000845"), asset?.previousClosePrice)
    }

    @Test
    fun `toExternal maps currency from market data`() = runTest {
        givenMarketPrice("VUSA.L", "Vanguard S&P 500", price = "75.00", previousClosePrice = "74.50", currency = "GBP")

        val id = repository.create("VUSA.L")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("GBP", asset?.currency)
    }

    @Test
    fun `toExternal maps previousClosePrice from market data`() = runTest {
        givenMarketPrice("TSLA", "Tesla Inc.", price = "250.00", previousClosePrice = "245.75")

        val id = repository.create("TSLA")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal("245.75"), asset?.previousClosePrice)
    }

    @Test
    fun `toExternal description comes from market data not entity name`() = runTest {
        givenMarketPrice("TICKER", "Full Company Name Inc.", price = "100", previousClosePrice = "99")

        val id = repository.create("TICKER")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("TICKER", asset?.name)
        assertEquals("Full Company Name Inc.", asset?.description)
    }

    @Test
    fun `toExternal handles asset name with special characters`() = runTest {
        givenMarketPrice("BRK.B", "Berkshire Hathaway B", price = "400", previousClosePrice = "398")

        val id = repository.create("BRK.B")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals("BRK.B", asset?.name)
        assertEquals("Berkshire Hathaway B", asset?.description)
    }

    @Test
    fun `toExternal after update reflects updated name`() = runTest {
        givenMarketPrice("GOOGL", "Alphabet Inc.", price = "175", previousClosePrice = "173")

        val id = repository.create("AAPL")
        repository.update(id, "GOOGL")

        val asset = repository.get(id)
        assertNotNull(asset)
        assertEquals(id, asset?.id)
        assertEquals("GOOGL", asset?.name)
        assertEquals("Alphabet Inc.", asset?.description)
        assertEquals(BigDecimal("175"), asset?.marketPrice)
    }

    @Test
    fun `toExternal handles very large market price`() = runTest {
        givenMarketPrice("BRK.A", "Berkshire Hathaway A", price = "650000.00", previousClosePrice = "649500.00")

        val id = repository.create("BRK.A")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal("650000.00"), asset?.marketPrice)
        assertEquals(BigDecimal("649500.00"), asset?.previousClosePrice)
    }

    @Test
    fun `toExternal handles zero market price`() = runTest {
        givenMarketPrice("DELIST", "Delisted Stock", price = "0", previousClosePrice = "5.00")

        val id = repository.create("DELIST")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal.ZERO, asset?.marketPrice)
        assertEquals(BigDecimal("5.00"), asset?.previousClosePrice)
    }

    @Test
    fun `observe toExternal maps all fields for each asset in list`() = runTest {
        givenMarketPrice("AAPL", "Apple Inc.", price = "190", previousClosePrice = "188")
        givenMarketPrice("MSFT", "Microsoft Corp.", price = "415", previousClosePrice = "412", pricePrecision = 3)

        repository.create("AAPL")
        repository.create("MSFT")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)

        val apple = result.first { it.name == "AAPL" }
        assertEquals("Apple Inc.", apple.description)
        assertEquals(BigDecimal("190"), apple.marketPrice)
        assertEquals("USD", apple.currency)
        assertEquals(BigDecimal("188"), apple.previousClosePrice)
        assertEquals(2, apple.pricePrecision)
        assertEquals(false, apple.isDeleted)

        val msft = result.first { it.name == "MSFT" }
        assertEquals("Microsoft Corp.", msft.description)
        assertEquals(BigDecimal("415"), msft.marketPrice)
        assertEquals("USD", msft.currency)
        assertEquals(BigDecimal("412"), msft.previousClosePrice)
        assertEquals(3, msft.pricePrecision)
        assertEquals(false, msft.isDeleted)
    }

    @Test
    fun `observe toExternal handles mixed market data availability`() = runTest {
        givenMarketPrice("AAPL", "Apple Inc.", price = "190", previousClosePrice = "188")
        fakeMarketPriceRepository.failingSymbols.add("MSFT")

        repository.create("AAPL")
        repository.create("MSFT")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(2, result.size)

        val apple = result.first { it.name == "AAPL" }
        assertEquals("Apple Inc.", apple.description)
        assertEquals(BigDecimal("190"), apple.marketPrice)
        assertEquals("USD", apple.currency)
        assertEquals(BigDecimal("188"), apple.previousClosePrice)
        assertEquals(2, apple.pricePrecision)

        val msft = result.first { it.name == "MSFT" }
        assertNull(msft.description)
        assertNull(msft.marketPrice)
        assertNull(msft.currency)
        assertNull(msft.previousClosePrice)
        assertNull(msft.pricePrecision)
    }

    @Test
    fun `observe toExternal empty list returns empty`() = runTest {
        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `observe toExternal preserves order from dao`() = runTest {
        repository.create("C")
        repository.create("A")
        repository.create("B")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(3, result.size)
        assertEquals(listOf("C", "A", "B"), result.map { it.name })
    }

    @Test
    fun `observe toExternal with searchQuery maps all market fields`() = runTest {
        givenMarketPrice("AAPL", "Apple Inc.", price = "190", previousClosePrice = "188")

        repository.create("AAPL")
        repository.create("GOOGL")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").first()

        assertEquals(1, result.size)
        val asset = result.first()
        assertEquals("AAPL", asset.name)
        assertEquals("Apple Inc.", asset.description)
        assertEquals(BigDecimal("190"), asset.marketPrice)
        assertEquals("USD", asset.currency)
        assertEquals(BigDecimal("188"), asset.previousClosePrice)
        assertEquals(2, asset.pricePrecision)
        assertEquals(false, asset.isDeleted)
    }

    @Test
    fun `toExternal maps different currencies from market data`() = runTest {
        givenMarketPrice("USD_ASSET", "USD Asset", price = "100", previousClosePrice = "99")
        givenMarketPrice("EUR_ASSET", "EUR Asset", price = "200", previousClosePrice = "198", currency = "EUR")
        givenMarketPrice("GBP_ASSET", "GBP Asset", price = "300", previousClosePrice = "297", currency = "GBP")

        repository.create("USD_ASSET")
        repository.create("EUR_ASSET")
        repository.create("GBP_ASSET")

        val result = repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals("USD", result.first { it.name == "USD_ASSET" }.currency)
        assertEquals("EUR", result.first { it.name == "EUR_ASSET" }.currency)
        assertEquals("GBP", result.first { it.name == "GBP_ASSET" }.currency)
    }

    @Test
    fun `toExternal maps equal price and previousClosePrice`() = runTest {
        givenMarketPrice("FLAT", "Flat Stock", price = "100.00", previousClosePrice = "100.00")

        val id = repository.create("FLAT")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal("100.00"), asset?.marketPrice)
        assertEquals(BigDecimal("100.00"), asset?.previousClosePrice)
    }

    @Test
    fun `toExternal maps market price higher than previous close`() = runTest {
        givenMarketPrice("UP", "Going Up", price = "110.00", previousClosePrice = "100.00")

        val id = repository.create("UP")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal("110.00"), asset?.marketPrice)
        assertEquals(BigDecimal("100.00"), asset?.previousClosePrice)
    }

    @Test
    fun `toExternal maps market price lower than previous close`() = runTest {
        givenMarketPrice("DOWN", "Going Down", price = "90.00", previousClosePrice = "100.00")

        val id = repository.create("DOWN")
        val asset = repository.get(id)

        assertNotNull(asset)
        assertEquals(BigDecimal("90.00"), asset?.marketPrice)
        assertEquals(BigDecimal("100.00"), asset?.previousClosePrice)
    }

    @Test
    fun `get returns null for non-existing asset after mapping`() = runTest {
        val asset = repository.get("does-not-exist")
        assertNull(asset)
    }
}
