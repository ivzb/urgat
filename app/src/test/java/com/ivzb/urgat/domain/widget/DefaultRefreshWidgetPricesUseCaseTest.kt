package com.ivzb.urgat.domain.widget

import com.ivzb.urgat.util.FakeLogger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultRefreshWidgetPricesUseCaseTest {

    private lateinit var fakeWidgetUpdater: FakeWidgetUpdater
    private lateinit var fakeRefreshQuotesUseCase: FakeRefreshQuotesUseCase

    private lateinit var refreshWidgetPrices: RefreshWidgetPricesUseCase

    @Before
    fun setUp() {
        fakeWidgetUpdater = FakeWidgetUpdater()
        fakeRefreshQuotesUseCase = FakeRefreshQuotesUseCase()

        refreshWidgetPrices = DefaultRefreshWidgetPricesUseCase(
            fakeWidgetUpdater,
            fakeRefreshQuotesUseCase,
            FakeLogger(),
        )
    }

    @Test
    fun `invoke should report nothing to refresh when no widget is placed`() = runTest {
        val actual = refreshWidgetPrices()

        assertEquals(RefreshWidgetPricesResult.NOTHING_TO_REFRESH, actual)
    }

    @Test
    fun `invoke should not fetch anything when no widget is placed`() = runTest {
        refreshWidgetPrices()

        assertTrue(fakeRefreshQuotesUseCase.calls.isEmpty())
    }

    @Test
    fun `invoke should not update any widget when no widget is placed`() = runTest {
        refreshWidgetPrices()

        assertTrue(fakeWidgetUpdater.updated.isEmpty())
    }

    @Test
    fun `invoke should report refreshed once the quotes arrive`() = runTest {
        placeAllWidgets()

        val actual = refreshWidgetPrices()

        assertEquals(RefreshWidgetPricesResult.REFRESHED, actual)
    }

    @Test
    fun `invoke should rebuild every placed widget`() = runTest {
        placeAllWidgets()

        refreshWidgetPrices()

        assertEquals(
            setOf(WidgetType.NET_WORTH, WidgetType.WATCHLIST),
            fakeWidgetUpdater.updated.toSet(),
        )
    }

    @Test
    fun `invoke should only refresh the widget which is actually placed`() = runTest {
        fakeWidgetUpdater.placedWidgets += WidgetType.NET_WORTH

        refreshWidgetPrices()

        assertEquals(listOf(setOf(WidgetType.NET_WORTH)), fakeRefreshQuotesUseCase.calls)
    }

    @Test
    fun `invoke should only rebuild the widget which is actually placed`() = runTest {
        fakeWidgetUpdater.placedWidgets += WidgetType.WATCHLIST

        refreshWidgetPrices()

        assertEquals(listOf(WidgetType.WATCHLIST), fakeWidgetUpdater.updated)
    }

    @Test
    fun `invoke should wait longer than the refresh button does`() = runTest {
        placeAllWidgets()

        refreshWidgetPrices()

        // nobody is watching a background refresh, so it is owed more patience than a tap
        assertEquals(
            DefaultRefreshWidgetPricesUseCase.FETCH_TIMEOUT_MILLIS,
            fakeRefreshQuotesUseCase.lastTimeoutMillis,
        )
    }

    @Test
    fun `invoke should report timed out when the quotes never arrive`() = runTest {
        placeAllWidgets()
        fakeRefreshQuotesUseCase.succeeds = false

        val actual = refreshWidgetPrices()

        assertEquals(RefreshWidgetPricesResult.TIMED_OUT, actual)
    }

    @Test
    fun `invoke should leave the widgets alone when it times out`() = runTest {
        placeAllWidgets()
        fakeRefreshQuotesUseCase.succeeds = false

        refreshWidgetPrices()

        // a rebuild would only replace good values with a loading state
        assertTrue(fakeWidgetUpdater.updated.isEmpty())
    }

    private fun placeAllWidgets() {
        fakeWidgetUpdater.placedWidgets += WidgetType.NET_WORTH
        fakeWidgetUpdater.placedWidgets += WidgetType.WATCHLIST
    }

}
