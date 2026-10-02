package com.ivzb.urgat.domain.widget

import com.ivzb.urgat.data.finance.FakeFinanceRepository
import com.ivzb.urgat.data.fire.NetWorth
import com.ivzb.urgat.data.forex.FakeForexRepository
import com.ivzb.urgat.data.marketData.FakeMarketPriceRepository
import com.ivzb.urgat.data.marketPrice.MarketPrice
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.domain.fire.FakeObserveNetWorthUseCase
import com.ivzb.urgat.domain.watchlist.FakeObserveWatchlistAssetsUseCase
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultRefreshQuotesUseCaseTest {

    private lateinit var fakeForexRepository: FakeForexRepository
    private lateinit var fakeMarketPriceRepository: FakeMarketPriceRepository
    private lateinit var fakeObserveNetWorthUseCase: FakeObserveNetWorthUseCase
    private lateinit var fakeObserveWatchlistAssetsUseCase: FakeObserveWatchlistAssetsUseCase
    private lateinit var fakeFinanceRepository: FakeFinanceRepository

    private lateinit var refreshQuotes: RefreshQuotesUseCase

    @Before
    fun setUp() {
        fakeForexRepository = FakeForexRepository()
        fakeMarketPriceRepository = FakeMarketPriceRepository()
        fakeObserveNetWorthUseCase = FakeObserveNetWorthUseCase()
        fakeObserveWatchlistAssetsUseCase = FakeObserveWatchlistAssetsUseCase()
        fakeFinanceRepository = FakeFinanceRepository()

        refreshQuotes = DefaultRefreshQuotesUseCase(
            fakeForexRepository,
            fakeMarketPriceRepository,
            fakeObserveNetWorthUseCase,
            fakeObserveWatchlistAssetsUseCase,
            fakeFinanceRepository,
        )
    }

    @Test
    fun `invoke should do nothing when asked for no types`() = runTest {
        seedCaches()

        val actual = refreshQuotes(types = setOf(), timeoutMillis = TIMEOUT)

        assertTrue(actual)
        assertTrue(fakeForexRepository.rates.isNotEmpty())
        assertTrue(fakeMarketPriceRepository.prices.isNotEmpty())
    }

    @Test
    fun `invoke should drop the cached quotes so fresh ones are fetched`() = runTest {
        seedCaches()
        fakeObserveNetWorthUseCase.emit(Async.Success(NET_WORTH))

        refreshQuotes(types = setOf(WidgetType.NET_WORTH), timeoutMillis = TIMEOUT)

        assertTrue(fakeMarketPriceRepository.prices.isEmpty())
    }

    @Test
    fun `invoke should drop the cached rates when the net worth is refreshed`() = runTest {
        seedCaches()
        fakeObserveNetWorthUseCase.emit(Async.Success(NET_WORTH))

        refreshQuotes(types = setOf(WidgetType.NET_WORTH), timeoutMillis = TIMEOUT)

        assertTrue(fakeForexRepository.rates.isEmpty())
    }

    @Test
    fun `invoke should keep the cached rates when only the watchlist is refreshed`() = runTest {
        seedCaches()
        fakeObserveWatchlistAssetsUseCase.emit(Async.Success(listOf(WATCHLIST_ASSET)))

        refreshQuotes(types = setOf(WidgetType.WATCHLIST), timeoutMillis = TIMEOUT)

        // the watchlist shows every asset in its own currency, so no rate it holds can be stale
        assertTrue(fakeForexRepository.rates.isNotEmpty())
    }

    @Test
    fun `invoke should report success once the quotes arrive`() = runTest {
        emitEverything()

        val actual = refreshQuotes(types = ALL_TYPES, timeoutMillis = TIMEOUT)

        assertTrue(actual)
    }

    @Test
    fun `invoke should not wait on the watchlist when only the net worth is asked for`() = runTest {
        // the watchlist is deliberately left loading - it must not hold the refresh up
        fakeObserveNetWorthUseCase.emit(Async.Success(NET_WORTH))

        val actual = refreshQuotes(types = setOf(WidgetType.NET_WORTH), timeoutMillis = TIMEOUT)

        assertTrue(actual)
    }

    @Test
    fun `invoke should not wait on the net worth when only the watchlist is asked for`() = runTest {
        fakeObserveWatchlistAssetsUseCase.emit(Async.Success(listOf(WATCHLIST_ASSET)))

        val actual = refreshQuotes(types = setOf(WidgetType.WATCHLIST), timeoutMillis = TIMEOUT)

        assertTrue(actual)
    }

    @Test
    fun `invoke should fail when the quotes never arrive`() = runTest {
        // nothing is emitted, so the net worth stays on Async.Loading forever

        val actual = refreshQuotes(types = setOf(WidgetType.NET_WORTH), timeoutMillis = TIMEOUT)

        assertFalse(actual)
    }

    @Test
    fun `invoke should fail when only one of the asked for types arrives`() = runTest {
        fakeObserveNetWorthUseCase.emit(Async.Success(NET_WORTH))

        val actual = refreshQuotes(types = ALL_TYPES, timeoutMillis = TIMEOUT)

        assertFalse(actual)
    }

    @Test
    fun `invoke should report success when the net worth cannot be calculated`() = runTest {
        // an error is still an answer - the widget has something to show and says so itself
        fakeObserveNetWorthUseCase.emit(Async.Error)

        val actual = refreshQuotes(types = setOf(WidgetType.NET_WORTH), timeoutMillis = TIMEOUT)

        assertTrue(actual)
    }

    @Test
    fun `invoke should observe the watchlist in the order the finance screen picked`() = runTest {
        emitEverything()

        refreshQuotes(types = setOf(WidgetType.WATCHLIST), timeoutMillis = TIMEOUT)

        // the widget renders the same list, so refreshing a different one would fetch the wrong
        // prices the moment a limit or filter is involved
        assertEquals(1, fakeObserveWatchlistAssetsUseCase.invocations)
    }

    private fun seedCaches() {
        fakeForexRepository.rates["EURUSD"] = "1.1".toBigDecimal()
        fakeMarketPriceRepository.prices["AAPL"] = MARKET_PRICE
    }

    private fun emitEverything() {
        fakeObserveNetWorthUseCase.emit(Async.Success(NET_WORTH))
        fakeObserveWatchlistAssetsUseCase.emit(Async.Success(listOf(WATCHLIST_ASSET)))
    }

    companion object {

        private const val TIMEOUT = 5_000L

        private val ALL_TYPES = setOf(WidgetType.NET_WORTH, WidgetType.WATCHLIST)

        private val NET_WORTH = NetWorth(
            amount = "125000".toBigDecimal(),
            currency = "EUR",
        )

        private val MARKET_PRICE = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = "100".toBigDecimal(),
            currency = "USD",
            previousClosePrice = "99".toBigDecimal(),
            pricePrecision = 2,
        )

        private val WATCHLIST_ASSET = WatchlistAsset(
            id = "id",
            name = "AAPL",
            description = null,
            marketPrice = null,
            currency = null,
            previousClosePrice = null,
            pricePrecision = null,
            isDeleted = false,
            createdAt = 0L,
        )

    }

}

