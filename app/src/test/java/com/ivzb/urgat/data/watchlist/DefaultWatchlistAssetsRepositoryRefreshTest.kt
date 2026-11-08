package com.ivzb.urgat.data.watchlist

import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultWatchlistAssetsRepositoryRefreshTest : DefaultWatchlistAssetsRepositoryTestBase() {

    @Test
    fun `isRefreshingFlow starts as false`() = runTest {
        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow becomes false after observe emits`() = runTest {
        repository.create("AAPL")

        repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow becomes false after observe with searchQuery emits`() = runTest {
        repository.create("AAPL")

        repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").first()

        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `refresh triggers re-emission with updated market data`() = runTest {
        repository.create("AAPL")

        val emissions = mutableListOf<List<WatchlistAsset>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        // First emission: no market price set
        assertTrue(emissions.isNotEmpty())
        assertNull(emissions.first().first().marketPrice)

        // Now set market price and trigger refresh
        givenMarketPrice("AAPL", "Apple", price = "150", previousClosePrice = "148")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        // Second emission should have market data
        assertTrue(emissions.size >= 2)
        val refreshedAsset = emissions.last().first()
        assertEquals("Apple", refreshedAsset.description)
        assertEquals(BigDecimal("150"), refreshedAsset.marketPrice)

        job.cancel()
    }

    @Test
    fun `refresh triggers re-emission for observe with searchQuery`() = runTest {
        repository.create("AAPL")

        val emissions = mutableListOf<List<WatchlistAsset>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "AAPL").collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        assertTrue(emissions.isNotEmpty())
        assertNull(emissions.first().first().marketPrice)

        givenMarketPrice("AAPL", "Apple Inc.", price = "155", previousClosePrice = "150")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        val refreshedAsset = emissions.last().first()
        assertEquals("Apple Inc.", refreshedAsset.description)
        assertEquals(BigDecimal("155"), refreshedAsset.marketPrice)

        job.cancel()
    }

    @Test
    fun `refresh re-fetches prices for all observed assets`() = runTest {
        repository.create("AAPL")
        repository.create("GOOGL")

        val emissions = mutableListOf<List<WatchlistAsset>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        val fetchCountBeforeRefresh = fakeMarketPriceRepository.fetchCount

        givenMarketPrice("AAPL", "Apple", price = "150", previousClosePrice = "148")
        givenMarketPrice("GOOGL", "Google", price = "2800", previousClosePrice = "2790")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        // Should have fetched price for each asset during refresh
        assertEquals(2, fakeMarketPriceRepository.fetchCount - fetchCountBeforeRefresh)

        val lastEmission = emissions.last()
        assertEquals("Apple", lastEmission.find { it.name == "AAPL" }?.description)
        assertEquals("Google", lastEmission.find { it.name == "GOOGL" }?.description)

        job.cancel()
    }

    @Test
    fun `multiple refreshes each trigger new emissions`() = runTest {
        repository.create("AAPL")

        val emissions = mutableListOf<List<WatchlistAsset>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()
        val emissionsAfterInitial = emissions.size

        givenMarketPrice("AAPL", "Apple", price = "150", previousClosePrice = "148")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size > emissionsAfterInitial)
        val emissionsAfterFirstRefresh = emissions.size

        // Update price and refresh again
        givenMarketPrice("AAPL", "Apple", price = "160", previousClosePrice = "150")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size > emissionsAfterFirstRefresh)
        assertEquals(BigDecimal("160"), emissions.last().first().marketPrice)

        job.cancel()
    }

    @Test
    fun `isRefreshingFlow resets to false after refresh completes`() = runTest {
        repository.create("AAPL")

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {}
        }

        advanceUntilIdle()
        assertEquals(false, repository.isRefreshingFlow.value)

        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertEquals(false, repository.isRefreshingFlow.value)

        job.cancel()
    }

    @Test
    fun `clearCache clears market data and triggers re-emission`() = runTest {
        givenMarketPrice("AAPL", "Apple", price = "150", previousClosePrice = "148")
        repository.create("AAPL")

        val emissions = mutableListOf<List<WatchlistAsset>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        // First emission has market data
        assertTrue(emissions.isNotEmpty())
        assertEquals(BigDecimal("150"), emissions.first().first().marketPrice)

        // clearCache clears prices, so after refresh market data should be null
        fakeMarketPriceRepository.clearCache()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        assertNull(emissions.last().first().marketPrice)

        job.cancel()
    }
}
