package com.ivzb.urgat.data.investment

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
class DefaultInvestmentsRepositoryRefreshTest : DefaultInvestmentsRepositoryTestBase() {

    @Test
    fun `isRefreshingFlow starts as false`() = runTest {
        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow becomes false after observe emits`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        repository.observe(limit = 10, dateRange = NO_DATE_RANGE).first()

        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `isRefreshingFlow becomes false after observe with searchQuery emits`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "SNP").first()

        assertEquals(false, repository.isRefreshingFlow.value)
    }

    @Test
    fun `refresh triggers re-emission with updated market data`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val emissions = mutableListOf<List<Investment>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        assertTrue(emissions.isNotEmpty())
        assertNull(emissions.first().first().description)

        givenMarketPrice("SNP 500", "S&P 500", "450")
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        val refreshedInvestment = emissions.last().first()
        assertEquals("S&P 500", refreshedInvestment.description)
        assertEquals(BigDecimal("450"), refreshedInvestment.marketPrice)

        job.cancel()
    }

    @Test
    fun `refresh triggers re-emission for observe with searchQuery`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val emissions = mutableListOf<List<Investment>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE, searchQuery = "SNP").collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        assertTrue(emissions.isNotEmpty())
        assertNull(emissions.first().first().description)

        givenMarketPrice("SNP 500", "S&P 500 ETF", "455")
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("455")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        val refreshedInvestment = emissions.last().first()
        assertEquals("S&P 500 ETF", refreshedInvestment.description)
        assertEquals(BigDecimal("455"), refreshedInvestment.marketPrice)

        job.cancel()
    }

    @Test
    fun `refresh re-fetches prices for all observed investments`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)
        repository.create("BRK.B", 440.toBigDecimal(), 330.toBigDecimal(), "etfs", "USD", 2)

        val emissions = mutableListOf<List<Investment>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        val fetchCountBeforeRefresh = fakeMarketPriceRepository.fetchCount

        givenMarketPrice("SNP 500", "S&P 500", "450")
        givenMarketPrice("BRK.B", "Berkshire", "380")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertEquals(2, fakeMarketPriceRepository.fetchCount - fetchCountBeforeRefresh)

        val lastEmission = emissions.last()
        assertEquals("S&P 500", lastEmission.find { it.name == "SNP 500" }?.description)
        assertEquals("Berkshire", lastEmission.find { it.name == "BRK.B" }?.description)

        job.cancel()
    }

    @Test
    fun `multiple refreshes each trigger new emissions`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val emissions = mutableListOf<List<Investment>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()
        val emissionsAfterInitial = emissions.size

        givenMarketPrice("SNP 500", "S&P 500", "450")
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size > emissionsAfterInitial)
        val emissionsAfterFirstRefresh = emissions.size

        givenMarketPrice("SNP 500", "S&P 500", "460")
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("460")
        fakeMarketPriceRepository.emitRefresh()
        advanceUntilIdle()

        assertTrue(emissions.size > emissionsAfterFirstRefresh)
        assertEquals(BigDecimal("460"), emissions.last().first().marketPrice)

        job.cancel()
    }

    @Test
    fun `isRefreshingFlow resets to false after refresh completes`() = runTest {
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

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
        givenMarketPrice("SNP 500", "S&P 500", "450")
        fakeInvestmentMarketPriceRepository.prices["SNP 500"] = BigDecimal("450")
        repository.create("SNP 500", 300.toBigDecimal(), 200.toBigDecimal(), "etfs", "USD", 1)

        val emissions = mutableListOf<List<Investment>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.observe(limit = 10, dateRange = NO_DATE_RANGE).collect {
                emissions.add(it)
            }
        }

        advanceUntilIdle()

        assertTrue(emissions.isNotEmpty())
        assertEquals("S&P 500", emissions.first().first().description)
        assertEquals(BigDecimal("450"), emissions.first().first().marketPrice)

        fakeMarketPriceRepository.clearCache()
        advanceUntilIdle()

        assertTrue(emissions.size >= 2)
        assertNull(emissions.last().first().description)

        job.cancel()
    }
}
